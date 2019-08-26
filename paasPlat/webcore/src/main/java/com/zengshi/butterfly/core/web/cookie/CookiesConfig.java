/**
 * 
 */
package com.zengshi.butterfly.core.web.cookie;

import java.util.Map;

/**
 *
 */
public class CookiesConfig {

	public static String LOGIN_INFO="__login_info__";
	
	
	private Map<String, CookieModel> configs;

	public CookiesConfig() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Map<String, CookieModel> getConfigs() {
		return configs;
	}

	public void setConfigs(Map<String, CookieModel> configs) {
		this.configs = configs;
	}
	
	public CookieModel getCookieModel(String cookie) {
		return configs.get(cookie);
	}
}
