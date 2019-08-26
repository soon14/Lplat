/**
 * 
 */
package com.zengshi.butterfly.core.config.provider;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zengshi.butterfly.core.config.Config;
import com.zengshi.butterfly.core.config.ConfigContainer;
import com.zengshi.butterfly.core.config.StringConfig;
import com.zengshi.butterfly.core.config.center.CenterPriorityEnum;
import com.zengshi.butterfly.core.config.center.ConfigCenter;
import com.zengshi.butterfly.core.config.center.ConfigCenterFactory;

/**
 * 该ConfigProvider提供的config信息为开发模式时采用的config
 * 2011-7-20
 */
public class DevConfigProvider extends AbstractConfigProvider {

	private static final Log logger = LogFactory.getLog(DevConfigProvider.class);
	
	public static final String FILE_PATH = "/dev-env.properties";
	
	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.provider.AbstractConfigProvider#getLogger()
	 */
	@Override
	public Log getLogger() {
		return logger;
	}
	
	public DevConfigProvider(){
		this.init(null, null);
	}
	
	/**
	 * 默认实现将configCenter设置为DevConfigCenter
	 */
	@Override
	public boolean init(ConfigContainer configContainer, CenterPriorityEnum centerPriority) {
		
		try {
			this.configCenter = ConfigCenterFactory.getConfigCenter(CenterPriorityEnum.DEV);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * DEVConfigProvider对象的setConfigCenter参数无效，默认实现将configCenter设置为DevConfigCenter
	 */
	@Override
	public void setConfigCenter(ConfigCenter configCenter)throws Exception{
		this.configCenter = ConfigCenterFactory.getConfigCenter(CenterPriorityEnum.DEV);
	}
	
	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.provider.AbstractConfigProvider#loadConfigs()
	 */
	@Override
	protected void loadConfigs() throws IOException {
		
		Properties properties = new Properties();
		properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(FILE_PATH));
		Iterator iterator =  properties.stringPropertyNames().iterator();
		while(iterator.hasNext()){
			String key = (String) iterator.next();
			this.configs.put(key, (Config) new StringConfig(key,properties.getProperty(key)));
		}
	}

}
