package com.zengshi.butterfly.core.reflect;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class DatasourceReflectUtil {
	private static Map<String ,Map<String, String>> configs=new HashMap<String, Map<String,String>>();
	
	private  static Map<String,Map<String,Method>> cache=new HashMap<String, Map<String,Method>>();
	
	private final static String KEY_ACTIVE_METHOD="__METHOD_ACTIVE__";
	private final static String KEY_IDLE_METHOD="__METHOD_IDLE__";
	
	static {
		Map<String,String> method=new HashMap<String,String>();
		method.put(KEY_ACTIVE_METHOD, "getNumActive");
		method.put(KEY_IDLE_METHOD, "getNumIdle");
		configs.put("org.apache.commons.dbcp.BasicDataSource", method);
		
		Map<String,String> c3p0method=new HashMap<String,String>();
		c3p0method.put(KEY_ACTIVE_METHOD, "getNumBusyConnections");
		c3p0method.put(KEY_IDLE_METHOD, "getNumIdleConnections");
		configs.put("com.mchange.v2.c3p0.ComboPooledDataSource",c3p0method);
	}
	
	
	
	public static Integer getActiveNumber(Object datasourceInstance) {
		// TODO Auto-generated method stub
		try {
			Method activeMethod= getMethod(datasourceInstance,KEY_ACTIVE_METHOD);
			Object count=activeMethod.invoke(datasourceInstance, new Object[]{});
			return Integer.parseInt(count.toString());
		} catch (Exception e) {
			
		}
		return null;
	}

	
	public static Integer getIdleNumber(Object datasourceInstance) {
		try {
			Method idleMethod= getMethod(datasourceInstance,KEY_IDLE_METHOD);
			Object count=idleMethod.invoke(datasourceInstance, new Object[]{});
			return Integer.parseInt(count.toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public static Method getMethod(Object datasource,String methodKey) throws SecurityException, NoSuchMethodException {
		Class clazz=datasource.getClass();
		String className=clazz.getName();
		if(!cache.containsKey(className)) {
			synchronized (cache) {
				if(!cache.containsKey(className)) {
					cache.put(className, new HashMap<String, Method>());
					cache.get(className).put(KEY_ACTIVE_METHOD, clazz.getMethod(configs.get(className).get(KEY_ACTIVE_METHOD),new Class[]{}));
					cache.get(className).put(KEY_IDLE_METHOD, clazz.getMethod(configs.get(className).get(KEY_IDLE_METHOD), new Class[]{}));
				}
			}
		}
		return cache.get(className).get(methodKey);
	}
}
