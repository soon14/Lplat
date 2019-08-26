package com.zengshi.butterfly.core.config;

import java.util.Properties;

/**
 * 获取配置数据
 *
 */
public class Application {

	private static Properties prop=new Properties();
	public  static String getValue(String key) {
		return prop.getProperty(key);
	}
	public static String getValue(String key,String defValue) {
		return prop.getProperty(key,defValue);
	}
	
	public static Integer getIntValue(String key) {
		return getIntValue(key,0);
	}
	
	public static Integer getIntValue(String key,int defValue) {
		return new Integer(getValue(key,defValue+""));
	}
	
	public static Long getLongValue(String key) {
		return getLongValue(key,0);
	}
	
	public static Long getLongValue(String key,long defValue) {
		return Long.parseLong(getValue(key,defValue+""));
	}
	
	public static Boolean getBoolean(String key) {
		return getBoolean(key,Boolean.FALSE);
	}
	
	public static Boolean getBoolean(String key,Boolean defValue) {
		return Boolean.parseBoolean(getValue(key, defValue.toString()));
	}
	
	
	public static void addProperty(Properties prop) {
		synchronized (prop) {
			Application.prop.putAll(prop);
		}
	}
}
