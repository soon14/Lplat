package com.zengshi.butterfly.core.web.security;


import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zengshi.butterfly.core.config.Application;
import com.zengshi.butterfly.core.security.encrypt.CryptoUtil;
import com.zengshi.butterfly.core.web.cookie.CookieModel;
import com.fasterxml.jackson.databind.ObjectMapper;


public class UserCookieUtil  {

    /** 配置的用户id */
    //private static final String COOKIE_AUTHOR_NAME = "_a_";
    
   // private static final String ENCRYPT_KEY ="ZhS38VT9$9mQ%TeIWF@iYtu8NKcIarBI";
    
    private static final String REQ_AUTHOR_KEY ="__AUTHOR_INFO__.COOKIE";
    

    public static void setUserCookie(HttpServletRequest request,CookieUser user) {
    	request.setAttribute(REQ_AUTHOR_KEY, user);
    }
    /**
     * 设置用户信息到cookie
     * 
     * @param request
     * @param user
     */
    public static void write(HttpServletRequest request, HttpServletResponse response,CookieUser user,CookieModel cookieModel) {
        write(request, response,user, -1,cookieModel);
    }
    
    public static void write(HttpServletRequest request, HttpServletResponse response,CookieUser user) {
        write(request, response,user, -1,createUserCookieModel());
    }
    
    
    /**
     * 设置用户信息到cookie
     * 
     * @param request
     * @param user
     */
    public static void write(HttpServletRequest request, HttpServletResponse response, CookieUser user, int expiry,CookieModel cookieModel) {
        if (request == null || user == null) {
            throw new IllegalArgumentException();
        }
        
        ObjectMapper om=new ObjectMapper();
        try {
        	//System.out.println("========================="+response.isCommitted());
        	user.setModifyTime(System.currentTimeMillis());
        	String authorInfo=om.writeValueAsString(user);
        	if(cookieModel.isEncrypt()) {
        		authorInfo=CryptoUtil.encrypt(authorInfo, cookieModel.getEncryptKey());
        		authorInfo=authorInfo.replaceAll("\n", " ");
        		
        	}
        	
        	Cookie cookie=new Cookie(cookieModel.getCookieName(), URLEncoder.encode(authorInfo,"utf-8"));
        	if(cookieModel.getPath() != null && !"".equals(cookieModel.getPath())) cookie.setPath(cookieModel.getPath());
        	if(cookieModel.getDomain() != null &&!"".equals(cookieModel.getDomain())) cookie.setDomain(cookieModel.getDomain());
        
        	cookie.setMaxAge(cookieModel.getAge());
        	response.addCookie(cookie);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * 取得cookie里的usercookie对象
     * 
     * @param request
     * @return
     */
    public static CookieUser getUserCookie(HttpServletRequest request,CookieModel cookieModel) {
    	CookieUser user=null;
    	user=(CookieUser)request.getAttribute(REQ_AUTHOR_KEY);
    	if(user != null) {
    		return user;
    	}
    	ObjectMapper om=new ObjectMapper();
    	
    	Cookie[] cookies=request.getCookies();
    	if(cookies != null && cookies.length > 0) {
    		for(Cookie cookie:request.getCookies()) {
        		if(cookieModel.getCookieName().equals(cookie.getName())) {
        			String authorInfo=cookie.getValue();
        			try {
    					authorInfo = URLDecoder.decode(authorInfo, "UTF-8");  

        				authorInfo=authorInfo.replaceAll(" ", "\n");
        				if(cookieModel.isEncrypt()) {

        					authorInfo=CryptoUtil.decrypt(authorInfo, cookieModel.getEncryptKey());
        				}
        				
    					if(authorInfo != null && !"".equals(authorInfo)) {
    						user=om.readValue(authorInfo, CookieUser.class);
    						break;
    					}
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
        		}
        	}
    	}
    	
       
    	if(user != null) {
    		request.setAttribute(REQ_AUTHOR_KEY, user);
    	}
        
        return user;

    }
    public  static CookieUser getUserCookie(HttpServletRequest request) {
    	return getUserCookie(request,createUserCookieModel());
    	
    }
    
    private static CookieModel createUserCookieModel() {
    	CookieModel cookieModel=new CookieModel();
    	cookieModel.setCookieName(Application.getValue(cookieName));
    	cookieModel.setDomain(Application.getValue(domain));
    	cookieModel.setPath(Application.getValue(path));
    	cookieModel.setAge(Application.getIntValue(age,-1));
    	cookieModel.setEncrypt(Application.getBoolean(encrypt,Boolean.TRUE));
    	cookieModel.setEncryptKey(Application.getValue(encryptKey));
    	cookieModel.setExpireTime(Application.getValue(expireTime));
    	return cookieModel;
    }
    
    static final String cookieName="project.cookie.userinfo.cookieName";
    static final String domain="project.cookie.userinfo.domain";
    static final String path="project.cookie.userinfo.path";
    static final String age="project.cookie.userinfo.age";
    static final String encrypt="project.cookie.userinfo.encrypt";
    static final String encryptKey="project.cookie.userinfo.encryptKey";
    static final String expireTime="project.cookie.userinfo.expireTime";
    
    
   /* public static String getSessionId(HttpServletRequest request) {
        
    	CookieUser user=getUserCookie(request);
    	if(user == null || user.getUserId() == 0l || user.getSessionId() ==null || "".equals(user.getSessionId())) {
    		throw new RuntimeException("用户未登录");
    	}
    	return user.getSessionId();
    }
    */
   

}
