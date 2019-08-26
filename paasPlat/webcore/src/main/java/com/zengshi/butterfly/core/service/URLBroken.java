package com.zengshi.butterfly.core.service;

import java.util.Map;



/**
 * 
 */

/**
 *
 */
public interface URLBroken {

	public static final String URL_LOGIN="url_login_page";
	public static final String URL_NOT_PERMIT="url_not_permit";
	public static final String URL_NOT_LOGIN="url_not_login";
	public static final String URL_EXPIRED="url_expired";
	public  String build(String resourceURL,String command) ;
	
	public String get(String url_key) ;
	
	public void put(String url_key,String url) ;
}
