package com.zengshi.butterfly.core.web.interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.OrderComparator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class DefaultInterceptor extends HandlerInterceptorAdapter implements InitializingBean,ApplicationContextAware{

	private ApplicationContext context;
	
	private List<CustomerInterceptor> customerInterceptors;
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		for(CustomerInterceptor interceptor:this.customerInterceptors) {
			boolean result=interceptor.preHandle(request, response, handler);
			if(!result) return result;
		}
		return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if(this.customerInterceptors != null) {
			for(int i=this.customerInterceptors.size()-1;i>=0;i--) {
				customerInterceptors.get(i).postHandle(request, response, handler, modelAndView);
			}
		}
		
		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		if(this.customerInterceptors != null) {
			for(int i=this.customerInterceptors.size()-1;i>=0;i--) {
				customerInterceptors.get(i).afterCompletion(request, response, handler, ex);
			}
		}
		super.afterCompletion(request, response, handler, ex);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(customerInterceptors == null || customerInterceptors.size() == 0) {
			Map<String,CustomerInterceptor> handlers=context.getBeansOfType(CustomerInterceptor.class, true, true);
			this.customerInterceptors=new ArrayList<CustomerInterceptor>();
			for(CustomerInterceptor handle:handlers.values()) {
				this.customerInterceptors.add(handle);
			}
		}
		
		if(this.customerInterceptors != null && this.customerInterceptors.size() >0) {
			OrderComparator.sort(this.customerInterceptors);
		}
		
		
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
		
	}
	

}
