package com.zengshi.butterfly.core.web.velocity;

import javax.servlet.ServletContext;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.core.Ordered;
import org.springframework.ui.velocity.SpringResourceLoader;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

import com.zengshi.butterfly.core.config.Application;

 


public class VelocityConfigurer  extends org.springframework.web.servlet.view.velocity.VelocityConfigurer implements Ordered{

	private int order = Integer.MAX_VALUE;
	
	private ServletContext servletContext;
	
	public int getOrder() {
		// TODO Auto-generated method stub
		return this.order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		// TODO Auto-generated method stub
		super.setServletContext(servletContext);
		
		this.servletContext =servletContext;
	}
	@Override
	protected void postProcessVelocityEngine(VelocityEngine velocityEngine) {
		// TODO Auto-generated method stub
		super.postProcessVelocityEngine(velocityEngine);//PathMatchingResourcePatternResolver
		velocityEngine.setApplicationAttribute(SpringResourceLoader.SPRING_RESOURCE_LOADER, new ServletContextResourcePatternResolver(this.servletContext));
		velocityEngine.setApplicationAttribute(SpringResourceLoader.SPRING_RESOURCE_LOADER_PATH, Application.getValue("view.velocity.path"));
	}

	
}
