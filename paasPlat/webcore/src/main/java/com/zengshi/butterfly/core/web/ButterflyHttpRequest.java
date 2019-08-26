package com.zengshi.butterfly.core.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ButterflyHttpRequest extends HttpServletRequestWrapper{

	private HttpServletRequest request;
	
	private String requestURI;
	
	
	
	public ButterflyHttpRequest(HttpServletRequest request) {
		super(request);
		this.request = request;
	}

	public ButterflyHttpRequest(HttpServletRequest request, String requestURI) {
		super(request);
		this.request = request;
		this.requestURI = requestURI;
	}

	

	
	@Override
	public String getServletPath() {
		if(this.requestURI != null) {
			return requestURI;
		}
		
		return this.request.getServletPath();
	}

}
