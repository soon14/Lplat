package com.zengshi.butterfly.core.web.spring;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.zengshi.paas.ConfigurationCenter;
import com.zengshi.paas.ConfigurationWatcher;
import com.zengshi.paas.PaasException;
import com.zengshi.butterfly.core.Refreshable;
import com.zengshi.butterfly.core.Visitor;
import com.zengshi.butterfly.core.config.Application;
import com.zengshi.butterfly.core.mapper.JsonMapper;

/**
 * 扩展spring的配置文件读取接口
 *
 */
public class ExtendedPropertyPlaceholderConfigurer extends
		PropertyPlaceholderConfigurer implements Visitor, Refreshable, ConfigurationWatcher  {
	
	private Properties props;

	private List<PropertyLoadListener> loadListeners;

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
	
	public List<PropertyLoadListener> getLoadListeners() {
		return loadListeners;
	}

	public void setLoadListeners(List<PropertyLoadListener> loadListeners) {
		this.loadListeners = loadListeners;
	}

	@Override
	protected void processProperties(
			ConfigurableListableBeanFactory beanFactory, Properties props)
			throws BeansException {
		if (this.props != null) {
			this.props.putAll(props);
		} else {
			this.props = props;
		}
		super.processProperties(beanFactory, this.props);
		
	}

	public Object getProperty(String key) {
		return props.get(key);
	}

	@Override
	protected void loadProperties(Properties props) throws IOException {
		for (PropertyLoadListener loadListener : loadListeners) {
			if (loadListener != null)
				loadListener.beforePropertyLoad(props);
			super.loadProperties(props);
			if (loadListener != null)
				loadListener.afterPropertyLoad(props);
		}
		
	}

	public void init() {
		try {
			if(StringUtils.isNotEmpty(confPath)){
				process(confCenter.getConfAndWatch(confPath, this));
			}
		} catch (PaasException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void refresh() {
		try {
			this.loadProperties(props);
		} catch (IOException e) {
			logger.error("load Propertie error ", e);
		}
	}

	@Override
	public String visitor() {
		try {
			return mapper.toJson(this.props);
		} catch (Exception e) {
			
		}
		return null;
	}
	
	private static ReentrantLock lock = new ReentrantLock(); 

	@Override
	public void process(String conf) {
		if(logger.isInfoEnabled()) {
			logger.info("new application configuration is received: " + conf);
		}		
		JSONObject jj = JSONObject.fromObject(conf);
		Map map = (HashMap) JSONObject.toBean(jj, HashMap.class);
		if (map != null && !map.isEmpty()) {
			try {
				lock.lock();
				if (props == null) {
					props = new Properties();
				}
				this.props.putAll(map);
				Application.addProperty(this.props);
			} finally {
				lock.unlock();
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
	
	
	
}
