package com.zengshi.butterfly.core.web;

public class URLBuilder {

	private static String contextPath;
	
	private static String domain;
	
	private static URLBuilder builder;
	
	private URLBuilder(String domin,String contextPath) {
		URLBuilder.domain=domin;
		URLBuilder.contextPath=contextPath;
	}
	
	public static String build(String uri,String extension) {
		String newUri=contextPath+"/"+uri;
		if(extension != null && !"".equals(extension)) {
			newUri=newUri+"."+extension;
		}
		return newUri;
	}
	
	public static String build(String uri,boolean custromer) {
		if(custromer) {
			StringBuffer url=new StringBuffer(contextPath);
			if(!uri.startsWith("/")) {
				url.append("/");
			}
			url.append(uri);
			return url.toString();
		}else {
			return build(uri);
		}
		
	}
	
	
	public static String build(String uri) {
		return build(uri,"html");
	}
	
	public static URLBuilder getInstance(String domin,String contextPath) {
		if(builder == null) builder=new URLBuilder(domin,contextPath);
		
		return  builder;
	}
	
 }
