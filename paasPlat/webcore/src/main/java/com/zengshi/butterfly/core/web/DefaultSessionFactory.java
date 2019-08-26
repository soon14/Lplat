package com.zengshi.butterfly.core.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class DefaultSessionFactory implements SessionFactory{

	@Override
	public HttpSession getSession(HttpServletRequest request) {
		return request.getSession();
	}

}
