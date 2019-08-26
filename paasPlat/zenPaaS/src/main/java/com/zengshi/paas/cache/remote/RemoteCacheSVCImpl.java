package com.zengshi.paas.cache.remote;

import com.zengshi.paas.ConfigurationCenter;
import com.zengshi.paas.ConfigurationWatcher;
import com.zengshi.paas.PaasException;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 远程缓存服务实现类
 *
 */
public class RemoteCacheSVCImpl implements ConfigurationWatcher,RemoteCacheSVC {
	
	private static final Logger log = Logger.getLogger(RemoteCacheSVCImpl.class);
	
	
	private static final String HOST_KEY = "host";
	private static final String PORT_KEY = "port";
	private static final String PASSWORD_KEY="password";
	private static final String TIMEOUT_KEY = "timeOut";
	private static final String MAXACTIVE_KEY = "maxActive";
	private static final String MAXIDLE_KEY = "maxIdle";
	private static final String MAXWAIT_KEY = "maxWait";
	private static final String TESTONBORROW_KEY = "testOnBorrow";
	private static final String TESTONRETURN_KEY = "testOnReturn";
	private static final String DBINDEX_KEY = "dbIndex";
	private static final String TIMEBETWEENEVICTIONRUNSMILLIS_KEY="timeBetweenEvictionRunsMillis";
	private static final String NUMTESTSPEREVICTIONRUN_KEY="numTestsPerEvictionRun";
	private static final String MINEVICTABLEIDLETIMEMILLIS_KEY="minEvictableIdleTimeMillis";
	private static final String TESTWHILEIDLE_KEY="testWhileIdle";
	private static final String SOFTMINEVICTABLEIDLETIMEMILLIS_KEY="softMinEvictableIdleTimeMillis";
	
	private String confPath = "/com/zengshi/paas/cache/conf";
	
	private String host = null;
	private String port = null;
	private String password=null;
	private String timeOut = null;
	private String maxActive = null;
	private String maxIdle = null;
	private String maxWait = null;
	private String testOnBorrow = null;
	private String testOnReturn = null;
	private String timeBetweenEvictionRunsMillis=null;
	private String numTestsPerEvictionRun=null;
	private String minEvictableIdleTimeMillis=null;
	private String testWhileIdle=null;
	private String softMinEvictableIdleTimeMillis=null;
	
	private RedisCacheClient redisCache=null;
	private ConfigurationCenter cc = null;
	private int dbIndex=0;
	
	public  RemoteCacheSVCImpl(){
		
	}
	
	public void init() {
		try {
			process(cc.getConfAndWatch(confPath, this));
		} catch (PaasException e) {
			e.printStackTrace();
		}
	}
	
	
	public void process(String conf) {
		if(log.isInfoEnabled()) {
			log.info("new remote cache configuration is received: " + conf);
		}
		JSONObject json = JSONObject.fromObject(conf);
		boolean changed = false;
		if(json.getString(HOST_KEY) != null && !json.getString(HOST_KEY).equals(host)) {
			changed = true;
			host = json.getString(HOST_KEY);
		}
		if(json.getString(PORT_KEY) != null && !json.getString(PORT_KEY).equals(port)) {
			changed = true;
			port = json.getString(PORT_KEY);
		}
		if(json.containsKey(PASSWORD_KEY) && !json.getString(PASSWORD_KEY).equals(password)){
			changed=true;
			password=json.getString(PASSWORD_KEY);
		}
		if(json.getString(TIMEOUT_KEY) != null && !json.getString(TIMEOUT_KEY).equals(timeOut)) {
			changed = true;
			timeOut = json.getString(TIMEOUT_KEY);
		}
		if(json.getString(MAXACTIVE_KEY) != null && !json.getString(MAXACTIVE_KEY).equals(maxActive)) {
			changed = true;
			maxActive = json.getString(MAXACTIVE_KEY);
		}
		if(json.getString(MAXIDLE_KEY) != null && !json.getString(MAXIDLE_KEY).equals(maxIdle)) {
			changed = true;
			maxIdle = json.getString(MAXIDLE_KEY);
		}
		if(json.getString(MAXWAIT_KEY) != null && !json.getString(MAXWAIT_KEY).equals(maxWait)) {
			changed = true;
			maxWait = json.getString(MAXWAIT_KEY);
		}
		if(json.getString(TESTONBORROW_KEY) != null && !json.getString(TESTONBORROW_KEY).equals(testOnBorrow)) {
			changed = true;
			testOnBorrow = json.getString(TESTONBORROW_KEY);
		}
		if(json.getString(TESTONRETURN_KEY) != null && !json.getString(TESTONRETURN_KEY).equals(testOnReturn)) {
			changed = true;
			testOnReturn = json.getString(TESTONRETURN_KEY);
		}
		if(json.getString(DBINDEX_KEY) != null && dbIndex != json.getInt(DBINDEX_KEY)) {
			dbIndex = json.getInt(DBINDEX_KEY);
		}
		if(null!=json.getString(TIMEBETWEENEVICTIONRUNSMILLIS_KEY) && !json.getString(TIMEBETWEENEVICTIONRUNSMILLIS_KEY).equals(timeBetweenEvictionRunsMillis)){
		    changed=true;
		    timeBetweenEvictionRunsMillis=json.getString(TIMEBETWEENEVICTIONRUNSMILLIS_KEY);
		}
		if(null!=json.getString(NUMTESTSPEREVICTIONRUN_KEY) && !json.getString(NUMTESTSPEREVICTIONRUN_KEY).equals(numTestsPerEvictionRun)){
		    changed=true;
		    numTestsPerEvictionRun=json.getString(NUMTESTSPEREVICTIONRUN_KEY);
		}
		if(null!=json.getString(MINEVICTABLEIDLETIMEMILLIS_KEY) && !json.getString(MINEVICTABLEIDLETIMEMILLIS_KEY).equals(minEvictableIdleTimeMillis)){
		    changed=true;
		    minEvictableIdleTimeMillis=json.getString(MINEVICTABLEIDLETIMEMILLIS_KEY);
		}
		if(null!=json.getString(TESTWHILEIDLE_KEY) && !json.getString(TESTWHILEIDLE_KEY).equals(testWhileIdle)){
		    changed=true;
		    testWhileIdle=json.getString(TESTWHILEIDLE_KEY);
		}
		if(null!=json.getString(SOFTMINEVICTABLEIDLETIMEMILLIS_KEY) && !json.getString(SOFTMINEVICTABLEIDLETIMEMILLIS_KEY).equals(softMinEvictableIdleTimeMillis)){
		    changed=true;
		    softMinEvictableIdleTimeMillis=json.getString(SOFTMINEVICTABLEIDLETIMEMILLIS_KEY);
		}
		if(changed) {
				redisCache = new RedisCacheClient(conf);
				if(log.isInfoEnabled()) {
					log.info("cache server address is changed to "+conf);
				}
		}
	}
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

	public void addItemToList(String key, Object object) {
		redisCache.addItemToList(dbIndex,key, object);
	}

	@SuppressWarnings("rawtypes")
	public List getItemFromList(String key) {
		return redisCache.getItemFromList(dbIndex,key);
	}

	public void addItem(String key, Object object) {
		redisCache.addItem(dbIndex,key, object);

	}

	@Override
	public boolean addItemNotExists(String key, Object object) {
		return redisCache.addItemNotExists(dbIndex, key, object);
	}

	@Override
	public void auth(String password) {
		redisCache.auth(password);
	}

	public void addItem(String key, Object object, int seconds) {
		redisCache.addItem(dbIndex,key, object, seconds);
	}

	public Object getItem(String key) {
		return redisCache.getItem(dbIndex,key);
	}

	public void delItem(String key) {
		redisCache.delItem(dbIndex,key);
	}

	public long getIncrement(String key) {
		return redisCache.getIncrement(dbIndex,key);
	}
	public long getDecrement(String key) {
		return redisCache.getDecrement(dbIndex,key);
	}

	public void setHashMap(String key, HashMap<String, String> map) {
		redisCache.setHashMap(dbIndex,key, map);
	}

	public Map<String, String> getHashMap(String key) {
		return redisCache.getHashMap(dbIndex,key);
	}

	public void addSet(String key, Set<String> set) {
		redisCache.addSet(dbIndex,key, set);
	}

	public Set<String> getSet(String key) {
		return redisCache.getSet(dbIndex,key);
	}
	
	public String flushDB() {
		return redisCache.flushDB(dbIndex);
	}
	
	public boolean exists(String key) {
		return redisCache.exists(dbIndex,key);
	}
	
	@SuppressWarnings("rawtypes")
	public List keys(String keyPattern) {
		return redisCache.keys(dbIndex, keyPattern);
	}
	
	public void hsetItem(String key, String field, Object object) {
		redisCache.hsetItem(dbIndex, key, field, object);
	}
	
	public Object hgetItem(String key, String field) {
		return redisCache.hgetItem(dbIndex, key, field);
	}
	
	public void hdelItem(String key, String field) {
		redisCache.hdelItem(dbIndex, key, field);
	}
	
	public void expire(String key, int seconds) {
		redisCache.expire(dbIndex, key, seconds);
	}
	
	public Set<String> hkeys(String key) {
		return redisCache.hkeys(dbIndex, key);
	}
	
	@Override
	public void addItemInt(String key, Long object) {
		redisCache.addItemInt(dbIndex, key, object);
	}

	@Override
	public void saddItem(String key, Object object) {
		redisCache.saddItem(dbIndex, key, object);
	}

    @Override
	public void saddStringMember(String key, String member){
		redisCache.saddStringMember(dbIndex, key, member);
	}

	public void sRemMember(String key , String member){
		redisCache.sRemMember(dbIndex, key , member);
	}

	@Override
	public void addItemFile(String key, byte[] file) {
		redisCache.addItemFile(key, file);
	}


	@Override
	public void zaddItem(String key, String item, double score) {
		redisCache.zaddItem(dbIndex, key, item, score);
	}

    @Override
    public Long zdelItem(String key, String item) {
        return redisCache.zdelItem(dbIndex, key, item);
    }

    @Override
    public Long zrank(String key, String item) {
        return redisCache.zrank(dbIndex, key, item);
    }

	@Override
	public Set<String> zgetItems(String key) {
		return redisCache.zgetItems(dbIndex, key);
	}

    @Override
    public Double zgetScore(String key,String item) {
        return redisCache.zgetScore(dbIndex, key,item);
    }

    @Override
    public Double zincrby(String key,String item,Double score) {
        return redisCache.zincrby(dbIndex, key,item,score);
    }

	@Override
	public Long publish(String channel, String message) {
		return redisCache.publish(channel,message);
	}

	@Override
	public Long publish(byte[] channel, byte[] message) {
		return redisCache.publish(channel,message);
	}

	@Override
	public void subscribe(JedisPubSub jedisPubSub, String... channels) {
		redisCache.subscribe(jedisPubSub,channels);
	}

	@Override
	public void subscribe(BinaryJedisPubSub jedisPubSub, byte[]... channels) {
		redisCache.subscribe(jedisPubSub,channels);
	}

	@Override
	public void psubscribe(BinaryJedisPubSub jedisPubSub, byte[]... patterns) {
		redisCache.psubscribe(jedisPubSub,patterns);
	}

	@Override
	public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
		redisCache.psubscribe(jedisPubSub,patterns);
	}

	@Override
	public void expireAt(String key, long unixTime) {
		redisCache.expireAt(dbIndex,key,unixTime);
	}

	@Override
	public boolean isSetMember(String key, String value) {
		return redisCache.sisMember(dbIndex,key,value);
	}

	@Override
	public Set<String> sUnion(String... keys) {
		return redisCache.sUnion(dbIndex, keys);
	}

	public static void main(String[] args) throws PaasException{
		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "paasContext.xml" });
		RemoteCacheSVC cacheSvc = (RemoteCacheSVC)ctx.getBean("cacheSvc");
		cacheSvc.addItem("key", "value");
		while(true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
