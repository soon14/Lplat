package com.zengshi.butterfly.core.web.helper;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpRequest;

import com.zengshi.butterfly.core.config.Application;
import com.zengshi.butterfly.core.web.RequestContext;



public class WebHelper {

	public static String printRequest(HttpServletRequest request) {
		StringBuffer requestConent=new StringBuffer();
		Enumeration<String>  attrs=request.getParameterNames();
		while(attrs.hasMoreElements()) {
			String attr=attrs.nextElement();
			requestConent.append("&").append(attr).append("=");
			String[] values=request.getParameterValues(attr);
			if(values != null && values.length >0) {
				for(String value:values) {
					requestConent.append(value);
				}
				if(requestConent.charAt(requestConent.length()-1) == ',') {
					requestConent.deleteCharAt(requestConent.length()-1);
				}
			}
		}
		
		return requestConent.toString();
	}
	
	public static boolean isDebugMode() {
		final String KEY_DEBUG_MODE="__PRODUCT.DEBUG_MODE__";
		HttpServletRequest request=RequestContext.getRequest();
		//1、判断request attribute
		String debugMode=request.getAttribute(KEY_DEBUG_MODE)== null?null:request.getAttribute(KEY_DEBUG_MODE).toString();
		if(debugMode!= null) {
			if("true".equals(debugMode)) return true;
		}
		//2、判断request parameter
		debugMode=request.getParameter(KEY_DEBUG_MODE);
		
		if("true".equals(debugMode)) {
			request.setAttribute(KEY_DEBUG_MODE, "true");
			return true;
		}
		//3、用户session
		debugMode=request.getSession().getAttribute(KEY_DEBUG_MODE)==null?null:request.getSession().getAttribute(KEY_DEBUG_MODE).toString();
		
		if("true".equals(debugMode)) {
			request.setAttribute(KEY_DEBUG_MODE, "true");
			return true;
		}
		//4、配置文件
		debugMode=Application.getValue(KEY_DEBUG_MODE);
		if("true".equals(debugMode)) {
			request.setAttribute(KEY_DEBUG_MODE, "true");
			return true;
		}
		//5、环境变量
		debugMode=System.getProperty(KEY_DEBUG_MODE, "false");
		
		return "true".equals(debugMode);
		
	}
	
	
}
