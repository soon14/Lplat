package com.zengshi.butterfly.app.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


/**
 * 定义了带filter的切面
 * 
 * 
 */
public abstract class FilterChainAware<T> {

	protected FilterChain<T> filterChain;
	
	protected FilterChain<T> customFilterChain;

	@Autowired
	public abstract void setFilterChain(@Qualifier("handlerFilterChain") FilterChain<T> filterChain);

	public abstract void setCustomFilterChain(FilterChain<T> customFilterChain);
	
	

}
