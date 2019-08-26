package com.zengshi.butterfly.core.web.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public abstract class AbstractCustomerHandlerInterceptor extends HandlerInterceptorAdapter implements CustomerInterceptor {

	private int order=Integer.MIN_VALUE;

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	
}
