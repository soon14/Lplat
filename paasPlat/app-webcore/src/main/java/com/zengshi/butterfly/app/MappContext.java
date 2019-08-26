/**
 * 
 */
package com.zengshi.butterfly.app;

import com.zengshi.butterfly.app.model.IAppDatapackage;

import java.util.HashMap;
import java.util.Map;

/**
 * @modify mler 将 MappContext 将RequestContext合并
 */
public class MappContext {
	
	public static final String MAPPCONTEXT_IMEI = "AILK_MAPP_CONTEXT_IMEI";//IMEI
	
	public static final String MAPPCONTEXT_DEVICE = "AILK_MAPP_CONTEXT_DEVICE";//终端类型
	
	public static final String MAPPCONTEXT_USER = "AILK_MAPP_CONTEXT_USER";//用户
	
	public static final String MAPPCONTEXT_RIGHT = "AILK_MAPP_CONTEXT_RIGHT";//用户权限
	
	public static final String MAPPCONTEXT_REQUEST_IP = "AILK_MAPP_CONTEXT_REQUEST_IP";//上下文请求IP
	
	public static final String MAPPCONTEXT_SESSIONID = "AILK_MAPP_CONTEXT_SESSIONID";//用户sessionId
	
	public static final String MAPPCONTEXT_REQUEST = "AILK_MAPP_CONTEXT_REQUEST";//请求报文对象
	
	public static final String MAPPCONTEXT_RESPONSE = "AILK_MAPP_CONTEXT_RESPONSE";//响应报文对象
	
	public static final String MAPPCONTEXT_REQUEST_STRING = "AILK_MAPP_CONTEXT_REQUEST_STRING";//请求报文
	
	public static final String MAPPCONTEXT_RESPONSE_STRING = "AILK_MAPP_CONTEXT_RESPONSE_STRING";//响应报文
	
	/** 将属性放入ThreadLocal中保存 **/
	private static ThreadLocal<Map<String, Object>> context = new ThreadLocal<Map<String, Object>>();

	public static void initContext(final IAppDatapackage request) {
		if(context.get() == null)
			context.set(new HashMap<String, Object>(0));
		
		context.get().put(MAPPCONTEXT_REQUEST, request);
	}
	
	public static void initContext(final IAppDatapackage request,Map<String, Object> context2) {
		if(context.get() == null)
			context.set(new HashMap<String, Object>(0));
		
		context.get().put(MAPPCONTEXT_REQUEST,request);
		
		addContext(context2);
	}

	public static void initContext(final String requestString,final IAppDatapackage request,Map<String, Object> context2) {
		if(context.get() == null)
			context.set(new HashMap<String, Object>(0));
		
		context.get().put(MAPPCONTEXT_REQUEST_STRING, requestString);
		context.get().put(MAPPCONTEXT_REQUEST, request);
		addContext(context2);
	}
	
	public static void initContext(Map<String, Object> context2) {
		if(context.get() == null)
			context.set(new HashMap<String, Object>(0));
		
//		context.get().clear();
		context.get().putAll(context2);
	}

	public static String getRequestString() 
	{
		if(context.get() == null || context.get().isEmpty())
			return null;
		
		return (String)context.get().get(MAPPCONTEXT_REQUEST_STRING);
	}

	public static void setRequestString(String requestString) 
	{
		if(context.get() == null)
			context.set(new HashMap<String, Object>(0));
		
		context.get().put(MAPPCONTEXT_REQUEST_STRING, requestString);
	}

	public static String getResponseString() {
		if(context.get() == null || context.get().isEmpty())
			return null;
		
		return (String)context.get().get(MAPPCONTEXT_RESPONSE_STRING);
	}

	public static void setResponseString(String responseString) {

		if(context.get() == null)
			context.set(new HashMap<String, Object>(0));
		
		context.get().put(MAPPCONTEXT_RESPONSE_STRING,responseString);
	}

	public static IAppDatapackage getResponse() {
		if(context.get() == null || context.get().isEmpty())
			return null;
		
		return (IAppDatapackage)context.get().get(MAPPCONTEXT_RESPONSE);
	}

	public static void setResponse(IAppDatapackage response) {
		if(context.get() == null)
			context.set(new HashMap<String, Object>(0));
		
		context.get().put(MAPPCONTEXT_RESPONSE,response);
	}

	public static IAppDatapackage getRequest() {
		if(context.get() == null || context.get().isEmpty())
			return null;
		
		return (IAppDatapackage)context.get().get(MAPPCONTEXT_REQUEST);
	}

	public static void setRequest(IAppDatapackage request) {
		
		if(context.get() == null)
			context.set(new HashMap<String, Object>(0));
		
		context.get().put(MAPPCONTEXT_REQUEST,request);
	}

	public static void setAttribute(String key, Object value) {

		if (context.get() == null)
			context.set(new HashMap<String, Object>(0));

		context.get().put(key, value);
	}
	
	public static void setAttribute(Map<String,Object> attributes)
	{
		if (context.get() == null)
			context.set(new HashMap<String, Object>(0));
		
		context.get().putAll(attributes);
	}

	public static Object getAttribute(String key) {

		if (context == null)
			return null;

		return context.get().get(key);
	}

	public static Map<String, Object> getContext() {
		return context.get();
	}

	public static void addContext(Map<String, Object> context2) {
		context.get().putAll(context2);
	}

	public static void clearContext() {
		context.remove();
	}
	
	public static void print()
	{
		if(context.get() == null || context.get().isEmpty())
		{
			System.out.println("========MAPPCONTEXT IS EMPTY");
			return;
		}
		
		for(String key : context.get().keySet())
		{
//			if(key.equals("MAPP_SESSION_USER"))
//			{
//				System.out.println("========MAPPCONTEXT key:"+key+":"+(getAttribute(key)==null?null:getAttribute(key).toString())+"=============");
//				continue;
//			}
			System.out.println("========MAPPCONTEXT key:"+key+":"+(getAttribute(key)==null?null:getAttribute(key).toString())+"=============");
		}
		
	}

}
