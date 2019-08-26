package com.zengshi.butterfly.core.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

public class DefaultRequestFactory implements HttpRequestFactory{

	@Override
	public HttpServletRequest getRequest(HttpServletRequest request,
			HttpServletResponse response) {
		return new HttpServletRequestWrapper(request);
	}

}
