package com.zengshi.butterfly.core.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.context.ServletContextAware;


import com.zengshi.paas.ConfigurationCenter;
import com.zengshi.paas.ConfigurationWatcher;
import com.zengshi.paas.PaasException;
import com.zengshi.butterfly.core.Refreshable;
import com.zengshi.butterfly.core.Visitor;
import com.zengshi.butterfly.core.mapper.JsonMapper;
import com.zengshi.butterfly.core.web.spring.PropertyLoadListener;

/**
 * 从zookeeper订阅信息
 *
 */
public class ZookeeperLoaderListener implements PropertyLoadListener,ServletContextAware, ConfigurationWatcher {
	
	private static Logger log = LoggerFactory.getLogger(ZookeeperLoaderListener.class);
	private ResourcePatternResolver resourceResolver=new PathMatchingResourcePatternResolver();

	private ConfigurationCenter confCenter;
	private String confPath = "";
	private static JsonMapper mapper = JsonMapper.nonDefaultMapper();
	public ConfigurationCenter getConfCenter() {
		return confCenter;
	}

	public void setConfCenter(ConfigurationCenter confCenter) {
		this.confCenter = confCenter;
	}

	public String getConfPath() {
		return confPath;
	}

	public void setConfPath(String confPath) {
		this.confPath = confPath;
	}
	
	public void init() {
		try {
			process(confCenter.getConfAndWatch(confPath, this));
		} catch (PaasException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public void process(String conf) {
		if(log.isInfoEnabled()) {
			log.info("new application configuration is received: " + conf);
		}		
		JSONObject jj = JSONObject.fromObject(conf);
		Map map = (HashMap) JSONObject.toBean(jj, HashMap.class);
		if (map != null && !map.isEmpty()) {
			try {
				
				//Application.addProperty();
			} finally {
			}
			/*synchronized (props) {
				if (props == null) {
					props = new Properties();
				}
				this.props.putAll(map);
				Application.addProperty(this.props);
			}*/
		}		
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.resourceResolver =new PathMatchingResourcePatternResolver();
	}

	@Override
	public void beforePropertyLoad(Properties props) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterPropertyLoad(Properties props) {
		Properties pp = new Properties();
		
	}

}
