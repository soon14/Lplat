package com.zengshi.butterfly.core.web.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

public interface ExceptionViewResolver {

	public ModelAndView resolver(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex);
}
