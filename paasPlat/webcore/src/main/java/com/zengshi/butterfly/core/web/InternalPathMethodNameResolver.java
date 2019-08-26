package com.zengshi.butterfly.core.web;

import javax.servlet.http.HttpServletRequest;

public class InternalPathMethodNameResolver extends org.springframework.web.servlet.mvc.multiaction.InternalPathMethodNameResolver{

	private static String DEFAULT_POST_PREIX="do";
	private static String DEFAULT_GET_PREIX="view";

	
	@Override
	protected String getHandlerMethodNameForUrlPath(String urlPath) {
		HttpServletRequest request=RequestContext.getRequest();
		return super.getHandlerMethodNameForUrlPath(request.getMethod().toUpperCase()+":"+urlPath);
	}

	
	
	@Override
	protected String getPrefix() {
		// TODO Auto-generated method stub
	
		HttpServletRequest request=RequestContext.getRequest();
		if("GET".equalsIgnoreCase(request.getMethod())) {
			return DEFAULT_GET_PREIX;
		}else if("POST".equalsIgnoreCase(request.getMethod())) {
			return DEFAULT_POST_PREIX;
		}else {
			throw new RuntimeException("not support method");
		}
		//return super.getPrefix();
	}

	@Override
	protected String postProcessHandlerMethodName(String methodName) {
		// TODO Auto-generated method stub
		String prefix=this.getPrefix();
		StringBuffer method=new StringBuffer();
		if(prefix != null && !"".equals(prefix)) {
			methodName=Character.toUpperCase(methodName.charAt(0))+methodName.substring(1);
			
			for(int i=0;i<methodName.length();i++) {
				if(methodName.charAt(i) == '_' || methodName.charAt(i) == '-') {
					method.append(Character.toUpperCase(methodName.charAt(i+1)));
					i++;
				}else {
					method.append(methodName.charAt(i));
				}
			}
		}else {
			method=new StringBuffer(methodName);
		}
		return super.postProcessHandlerMethodName(method.toString());
	}

	
	
}
