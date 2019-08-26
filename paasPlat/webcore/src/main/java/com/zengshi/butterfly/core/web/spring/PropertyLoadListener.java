package com.zengshi.butterfly.core.web.spring;

import java.util.Properties;

public interface PropertyLoadListener {

	public void beforePropertyLoad(Properties props) ;
	
	public void afterPropertyLoad(Properties props);
}
