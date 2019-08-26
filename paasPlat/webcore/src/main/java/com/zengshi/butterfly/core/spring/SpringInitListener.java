/**
 * 
 */
package com.zengshi.butterfly.core.spring;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.zengshi.butterfly.core.event.ServletBootstrapListener;


/**
 *
 */
public class SpringInitListener implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		BeanFactory.setContext(WebApplicationContextUtils.getWebApplicationContext(event.getServletContext()));
		
		
		ApplicationContext context=WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
		Map<String, ServletBootstrapListener> listeners=context.getBeansOfType(ServletBootstrapListener.class, true, true);
		for(Iterator<String> keys=listeners.keySet().iterator();keys.hasNext();) {
			listeners.get(keys.next()).onStartup(event);
		}
		
		
		
	}

}
