package com.zengshi.butterfly.core.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface SessionFactory {

	public  HttpSession getSession(HttpServletRequest request);

}
