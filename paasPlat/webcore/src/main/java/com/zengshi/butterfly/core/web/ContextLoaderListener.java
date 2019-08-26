package com.zengshi.butterfly.core.web;

import javax.servlet.ServletContext;

import org.springframework.web.context.ConfigurableWebApplicationContext;

public class ContextLoaderListener extends
		org.springframework.web.context.ContextLoaderListener {

	@Override
	protected void customizeContext(ServletContext servletContext,
			ConfigurableWebApplicationContext applicationContext) {
		// TODO Auto-generated method stub
		super.customizeContext(servletContext, applicationContext);
		applicationContext.setConfigLocation("classpath*:applicationContext.xml,classpath*:com/zengshi/butterfly/**/*applicationContext-butterfly.xml");
	}



}
