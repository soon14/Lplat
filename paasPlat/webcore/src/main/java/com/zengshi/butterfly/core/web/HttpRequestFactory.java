package com.zengshi.butterfly.core.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HttpRequestFactory {
	
	public HttpServletRequest getRequest(HttpServletRequest request, HttpServletResponse response);
}
