package com.zengshi.butterfly.app;

import java.util.HashSet;
import java.util.Set;

public abstract class MappConstant {

	public static final String IP_LOCALHOST = "127.0.0.1";
	
	/**session 参数**/
	public static final Set<String> sessionKeys = new HashSet<String>(0);
	
	public static final String MAPP_SESSION_USER = "MAPP_SESSION_USER";
	
	public static final String MAPP_SESSION_RIGHT = "MAPP_SESSION_RIGHT";
	
	public static final String MAPP_SESSION_IMEI = "MAPP_SESSION_IMEI";
	
	public static final String MAPP_SESSION_DEVICE = "MAPP_SESSION_DEVICE";
	
	static {
		sessionKeys.add(MAPP_SESSION_USER);
		sessionKeys.add(MAPP_SESSION_RIGHT);
		sessionKeys.add(MAPP_SESSION_IMEI);
		sessionKeys.add(MAPP_SESSION_DEVICE);
	}
	
}
