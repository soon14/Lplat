package com.zengshi.paas.session;

import com.zengshi.paas.ConfigurationCenter;
import com.zengshi.paas.ConfigurationWatcher;
import com.zengshi.paas.PaasException;
import com.zengshi.paas.cache.remote.RedisCacheClient;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 统一Session管理器
 *
 */
public class SessionManager implements ConfigurationWatcher {
	private Logger log = Logger.getLogger(SessionManager.class);
	private static final String SESSION_ID_PREFIX = "R_JSID_";
	//private static final String SESSION_ID_COOKIE = "JSESSIONID";
	private static final String EXPIRATIONUPDATEINTERVAL_KEY = "expirationUpdateInterval";
	private static final String MAXINACTIVEINTERVAL_KEY = "maxInactiveInterval";
	private static final String HOST_KEY = "host";
	private static final String PORT_KEY = "port";
	private static final String TIMEOUT_KEY = "timeOut";
	private static final String MAXACTIVE_KEY = "maxActive";
	private static final String MAXIDLE_KEY = "maxIdle";
	private static final String MAXWAIT_KEY = "maxWait";
	private static final String TESTONBORROW_KEY = "testOnBorrow";
	private static final String TESTONRETURN_KEY = "testOnReturn";
	private static final String DBINDEX_KEY = "dbIndex";
	private static final String COOKIE_DOMAIN = "cookieDomain";
	private static final String COOKIE_SESSION_ID = "cookieSessionId";
	private static final String COOKIE_PATH = "cookiePath";
	private String host = null;
	private String port = null;
	private String timeOut = null;
	private String maxActive = null;
	private String maxIdle = null;
	private String maxWait = null;
	private String testOnBorrow = null;
	private String testOnReturn = null;
	private String cookieDomain = null;
	private RedisCacheClient cacheClient = null;
	private String cookieSessionId = "ECP_JSESSIONID";
	private String cookiePath = "";
	private int dbIndex = 0;
	private int expirationUpdateInterval = 300;
	private int maxInactiveInterval = 10800;
	private String confPath = "";
	private ConfigurationCenter cc = null;

	public ConfigurationCenter getCc() {
		return cc;
	}

	public void setCc(ConfigurationCenter cc) {
		this.cc = cc;
	}

	public String getConfPath() {
		return confPath;
	}

	public void setConfPath(String confPath) {
		this.confPath = confPath;
	}

	public CacheHttpSession createSession(
			SessionHttpServletRequestWrapper request,
			HttpServletResponse response, boolean create) {
		String sessionId = getRequestedSessionId(request);
		CacheHttpSession session = null;
		if ((StringUtils.isEmpty(sessionId)) && (!create))
			return null;
		if (StringUtils.isNotEmpty(sessionId)) {
			session = loadSession(sessionId, request, response);
		}
		if ((session == null) && (create)) {
			session = createEmptySession(request, response);
		}
		return session;
	}

	private String getRequestedSessionId(HttpServletRequestWrapper request) {
		Cookie[] cookies = request.getCookies();
		if ((cookies == null) || (cookies.length == 0))
			return null;
		//String value=null;
		for (Cookie cookie : cookies) {
			if (this.cookieSessionId.equals(cookie.getName())){
			    return cookie.getValue();
			}
		}
		return null;
	}

	private CacheHttpSession createEmptySession(
			SessionHttpServletRequestWrapper request,
			HttpServletResponse response) {
		CacheHttpSession session = new CacheHttpSession();
		session.id = createSessionId();
		session.isNew = true;
		session.creationTime = System.currentTimeMillis();
		session.setSessionManager(this);
		session.lastAccessedTime = System.currentTimeMillis();
		session.setRequest(request);
		session.setResponse(response);
        
		this.setItemToCache(session.id, "lastAccessedTime" + session.id,
				session.lastAccessedTime);
		this.expireItemToCache(session.id, this.maxInactiveInterval);
		
		if (this.log.isDebugEnabled())
			this.log.debug("CacheHttpSession Create [ID=" + session.id + "]");
		saveCookie(session, request, response);
		return session;
		
	}

	private String createSessionId() {
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}
	
	/**
	 * 
	 * saveCookie: 将session中的sessionId 写入 cookie; <br/> 
	 * 
	 * @param session
	 * @param request
	 * @param response 
	 * @since JDK 1.6
	 */
	public void saveCookie(CacheHttpSession session,
			HttpServletRequestWrapper request, HttpServletResponse response) {
	    
	    Cookie cookie = null;
        ///如果是新的，则创建一个cookie ；否则 从request中 获取，如果 request中没有，则退出；
        if(session.isNew){
            cookie = new Cookie(this.cookieSessionId, null);
            cookie.setValue(session.getId());
        } else {
            ///从 request 中获取；
            Cookie[] cookies = request.getCookies();
            if ((cookies == null) || (cookies.length == 0)){
                return;
            } else {
                for (Cookie ck : cookies) {
                    if (this.cookieSessionId.equals(ck.getName())){
                        cookie = ck;
                        break;
                    }
                }
            }
            ///如果还是没有，则还是新建一个；
            if(cookie == null){
                cookie = new Cookie(this.cookieSessionId, null);
                cookie.setValue(session.getId());
            }
        }
        
        //失效；
        if(session.expired){
            cookie.setMaxAge(0);
        } else {
            cookie.setMaxAge(-1);
        }
        
        //将 cookie 保存到 response ；额外设置一些 domain 和 cookiePath 相关的内容； 
        this.saveCookieToResponse(cookie, request, response);

		if (this.log.isDebugEnabled())
			this.log.debug("CacheHttpSession saveCookie [ID=" + session.id	+ "]");
	}
	
	/**
	 * 
	 * saveCookieToResponse: 将 cookie 信息 写入 response <br/> 
	 * 
	 * @param cookie
	 * @param request
	 * @param response 
	 * @since JDK 1.6
	 */
	public void saveCookieToResponse(Cookie cookie ,HttpServletRequest request, HttpServletResponse response){
	    ///写 cookieDomain
        if (!"".equals(cookieDomain)) {
            cookie.setDomain(cookieDomain);
        }
        
        /**
         * 写cookiePath
         * 如果zk配置的cookiePath为空，那么就根据contextPath()来写；否则，就根据cookiePath写；
         * add by yugn 2015.11.21
         */
        if(StringUtils.isEmpty(cookiePath)){
            cookie.setPath(StringUtils.isBlank(request.getContextPath()) ? "/" : request.getContextPath()+"/");
        } else {
            cookie.setPath(cookiePath);
        }
        
        response.addCookie(cookie);
        
        //this.log.debug("====append cookie====key:"+cookie.getName()+"value:"+cookie.getValue());
	}

	private CacheHttpSession loadSession(String sessionId,
			SessionHttpServletRequestWrapper request,
			HttpServletResponse response) {
		CacheHttpSession session;
		Object o = this.getItemFromCache(sessionId, "lastAccessedTime"
				+ sessionId);
		if (o == null) {
			this.log.debug("Session " + sessionId + " not found in Redis");
			session = null;
		} else {
			session = new CacheHttpSession();
			session.id = sessionId;
			session.setSessionManager(this);
			session.setRequest(request);
			session.setResponse(response);
			session.isNew = false;
			session.lastAccessedTime = (Long) this.getItemFromCache(session.id,
					"lastAccessedTime" + session.id);
			/**
			 * 调用一次写入Cookie；为了和spring security配合的时候，
			 * spring sercurity 每次会重新生成一个Session，
			 *    会将之前的session.invalidate() (导致写入 cookie 的 maxAge(0););  
			 *    并调用 request.getSession(true); 而在SessionManager中，并没有实际上的创建新的Session（session.id与原来一致）；
			 *    而没有重新保存Cookie，导致客户端Cookie失效；
			 *    
			 *    -----------
			 *    2015.11.8  modify yugn
			 *    去掉 saveCookie() 的方式；修改为在 session.invalidate() 的时候，清空session 在 redis的缓存；使得用旧的sessionId获取不到数据，必须进行一次重新生成；
			 *    对原来的代码逻辑没有看清楚，导致了这里加上 saveCookie的动作，加重了负担；；罪过。。。罪过！
			 */
			//saveCookie(session, request, response);

			this.updateExpireItemToCache(session);
		}
		if (this.log.isDebugEnabled())
			this.log.debug("CacheHttpSession Load [ID=" + sessionId + ",exist="
					+ (session != null) + "]");
		return session;
	}

	private String generatorSessionKey(String sessionId) {
		return SESSION_ID_PREFIX.concat(sessionId);
	}

	public SessionManager() {
	}

	public void init() {
		try {
			process(cc.getConfAndWatch(confPath, this));
		} catch (PaasException e) {
			log.error(e.getMessage(),e);
		}
	}

	public void process(String conf) {
		if (log.isInfoEnabled()) {
			log.info("new session configuration is received: " + conf);
		}
		JSONObject json = JSONObject.fromObject(conf);
		boolean changed = false;
		if (json.getString(HOST_KEY) != null
				&& !json.getString(HOST_KEY).equals(host)) {
			changed = true;
			host = json.getString(HOST_KEY);
		}
		if (json.getString(PORT_KEY) != null
				&& !json.getString(PORT_KEY).equals(port)) {
			changed = true;
			port = json.getString(PORT_KEY);
		}
		if (json.getString(TIMEOUT_KEY) != null
				&& !json.getString(TIMEOUT_KEY).equals(timeOut)) {
			changed = true;
			timeOut = json.getString(TIMEOUT_KEY);
		}
		if (json.getString(MAXACTIVE_KEY) != null
				&& !json.getString(MAXACTIVE_KEY).equals(maxActive)) {
			changed = true;
			maxActive = json.getString(MAXACTIVE_KEY);
		}
		if (json.getString(MAXIDLE_KEY) != null
				&& !json.getString(MAXIDLE_KEY).equals(maxIdle)) {
			changed = true;
			maxIdle = json.getString(MAXIDLE_KEY);
		}
		if (json.getString(MAXWAIT_KEY) != null
				&& !json.getString(MAXWAIT_KEY).equals(maxWait)) {
			changed = true;
			maxWait = json.getString(MAXWAIT_KEY);
		}
		if (json.getString(TESTONBORROW_KEY) != null
				&& !json.getString(TESTONBORROW_KEY).equals(testOnBorrow)) {
			changed = true;
			testOnBorrow = json.getString(TESTONBORROW_KEY);
		}
		if (json.getString(TESTONRETURN_KEY) != null
				&& !json.getString(TESTONRETURN_KEY).equals(testOnReturn)) {
			changed = true;
			testOnReturn = json.getString(TESTONRETURN_KEY);
		}
		if (json.getString(DBINDEX_KEY) != null
				&& !json.getString(DBINDEX_KEY).equals(dbIndex)) {
			dbIndex = json.getInt(DBINDEX_KEY);
		}
		if (json.getInt(EXPIRATIONUPDATEINTERVAL_KEY) != expirationUpdateInterval) {
			expirationUpdateInterval = json
					.getInt(EXPIRATIONUPDATEINTERVAL_KEY);
		}
		if (json.getInt(MAXINACTIVEINTERVAL_KEY) != maxInactiveInterval) {
			maxInactiveInterval = json.getInt(MAXINACTIVEINTERVAL_KEY);
		}
		if (null==cookieDomain || !(cookieDomain.equals(json.getString(COOKIE_DOMAIN)))) {
			cookieDomain = json.getString(COOKIE_DOMAIN);
		}
		if(json.containsKey(COOKIE_SESSION_ID) && !(cookieSessionId.equals(json.getString(COOKIE_SESSION_ID)))){
		    String tmp = json.getString(COOKIE_SESSION_ID);
		    if(StringUtils.isNotBlank(tmp)){
		        cookieSessionId = tmp;
		    }
		}
		
		if(json.containsKey(COOKIE_PATH) && !(cookiePath.equals(json.getString(COOKIE_PATH)))){
            if(StringUtils.isNotBlank(json.getString(COOKIE_PATH))){
                cookiePath = json.getString(COOKIE_PATH);
            }
		}
		
		if (changed) {
			cacheClient = new RedisCacheClient(conf);
			if (log.isInfoEnabled()) {
				log.info("cache server address is changed to " + conf);
			}
		}
	}

	public void removeSessionFromCache(String id) {
		cacheClient.delItem(dbIndex, generatorSessionKey(id));
	}

	public void setItemToCache(String id, String k, Object v) {
		cacheClient.hsetItem(dbIndex, generatorSessionKey(id), k, v);
	}

	public void delItemFromCache(String id, String k) {
		cacheClient.hdelItem(dbIndex, generatorSessionKey(id), k);
	}

	public Object getItemFromCache(String id, String k) {
		return cacheClient.hgetItem(dbIndex, generatorSessionKey(id), k);
	}

	public void expireItemToCache(String id, int seconds) {
		cacheClient.expire(dbIndex, generatorSessionKey(id), seconds);
	}

	public Set<String> getItemNamesFromCache(String id) {
		Set<String> itemNames = cacheClient.hkeys(dbIndex,
				generatorSessionKey(id));
		itemNames.remove("lastAccessedTime" + id); // 移除最后访问时间
		return itemNames;
	}

	public void updateExpireItemToCache(CacheHttpSession session) {
		int updateInterval = (int) ((System.currentTimeMillis() - session.lastAccessedTime) / 1000L);
		if (updateInterval < this.expirationUpdateInterval)
			return; // 访问间隔时间小于设置时间，直接返回，不更新最大有存活时间和最后访问时间
		else {
			if (session.expired)
				this.removeSessionFromCache(session.getId());
			else {
				this.setItemToCache(session.getId(), "lastAccessedTime"
						+ session.getId(), System.currentTimeMillis());
				this.expireItemToCache(session.getId(),
						this.maxInactiveInterval);
			}
		}
	}

	public static void main(String[] args) throws PaasException {
		Set<String> attrNames = new HashSet<String>();
		attrNames.add("a");
		attrNames.add("b");
		attrNames.remove("a");
		String[] a = new String[attrNames.size()];
		String[] b = (String[]) attrNames.toArray(a);
		for (int i = 0; i < b.length; i++) {
			System.out.println(b[i]);
		}
		// System.out.println(attrNames.toArray(a).toString());
		// Enumeration b = (Enumeration)attrNames;
		// Iterator iterator = b.

	}

}
