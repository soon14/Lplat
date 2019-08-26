package com.zengshi.butterfly.app.filter;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 实现带filter的切面定义Aspect模块
 * @param <T>
 *
 */
@Aspect
@Component
public class AbstractHandlerAspect<T> extends FilterChainAware<T> {

	@Override
	public void setFilterChain(FilterChain<T> filterChain) {
		
		
	}

	@Override
	public void setCustomFilterChain(FilterChain<T> customFilterChain) {
		// TODO Auto-generated method stub
		
	}

	

}
