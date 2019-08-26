package com.zengshi.butterfly.core.web.security;

public class LoginInfoHelper {

	private static ThreadLocal<AuthorUserPrincipal> userinfo=new ThreadLocal<AuthorUserPrincipal>();
	
	public static void set(AuthorUserPrincipal user) {
		userinfo.set(user);
	}
	public static AuthorUserPrincipal get() {
		return userinfo.get();
	}
	
	public boolean isLogin() {
		return (userinfo.get() !=null) && 
				(userinfo.get().getLoginId() != null) && 
				(!"".equals(userinfo.get().getLoginId()));
	}
	
	public static String getLoginId() {
        return userinfo.get().getLoginId();
    }
	
	public static String getNickName() {
        return userinfo.get().getNickName();
    }
	
	public static Integer getLoginIdType() {
        return userinfo.get().getLoginIdType();
    }
	
	public static String getClientIP() {
		return userinfo.get().getClientIP();
	}
}
