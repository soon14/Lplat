package com.zengshi.butterfly.core.event;

import javax.servlet.ServletContextEvent;

public interface ServletBootstrapListener {

	public void onStartup(ServletContextEvent event);
}
