/**
 * 
 */
package com.zengshi.butterfly.core.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zengshi.butterfly.core.config.center.CenterPriorityEnum;
import com.zengshi.butterfly.core.config.center.ConfigCenter;
import com.zengshi.butterfly.core.config.provider.ConfigProvider;

/**
 * 配置管理器
 * 2011-10-9
 */
public class ConfigManagerDefault implements ConfigManager {
	
	public static Log logger = LogFactory.getLog(ConfigManagerDefault.class);
	/**
	 * 配置容器
	 */
	private ConfigContainer configContainer;
	/**
	 * 配置提供者
	 */
	private List<ConfigProvider> providers;
	
	private static ConfigManager configManager;
	
	private ConfigManagerDefault() throws Exception{
		/**
		 * 初始化配置容器,添加配置中心
		 */
		logger.debug("获取configContainer实例");
		this.configContainer = ConfigContainerDefault.getInstance();
	}

	/**
	 * 获取配置管理器实例
	 * @return
	 * @throws Exception
	 */
	public static ConfigManager getInstance() throws Exception{
		
		if(configManager == null){
			
			logger.debug("没有现成的ConfigManager实例，new一个");
			configManager = new ConfigManagerDefault();
			
			//configManager.build();
		}else{
			//logger.debug("使用现有的ConfigManager实例");
		}
		return configManager;
	}
	
	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.temp.ConfigManager#getConfigContainer()
	 */
	@Override
	public ConfigContainer getConfigContainer() {
		return this.configContainer;
	}

	
	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.temp.ConfigManager#addProvider(com.zengshi.cu.config.temp.ConfigProvider)
	 */
	@Override
	public boolean addProvider(ConfigProvider configProvider) throws Exception {
		
		
		
		if(this.providers == null ){
			this.providers = new ArrayList<ConfigProvider>();
		}
		
		if(configProvider == null){
			return false;
		}
		
		if(configProvider.getConfigCenter() == null){
			String errMsg = "该configProvider未关联configCenter";
			logger.error(errMsg);
			throw new RuntimeException(errMsg);
		}
		
		logger.debug("添加configProvider:"+configProvider.getClass()+",  优先级别为："+configProvider.getConfigCenter().getPriority());
		
		return this.providers.add(configProvider);
	}


	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.temp.ConfigManager#addProvider(com.zengshi.cu.config.temp.ConfigProvider, int)
	 */
	@Override
	public boolean addProvider(ConfigProvider configProvider, CenterPriorityEnum centerPriority) throws Exception {
		logger.debug("添加configProvider:"+configProvider.getClass()+",  优先级别为："+centerPriority.getPriority());
		
		if(this.providers == null ){
			this.providers = new ArrayList<ConfigProvider>();
		}
		
		ConfigCenter configCenter = this.configContainer.findConfigCenter(centerPriority);
		
		if(configCenter == null){
			String errMsg = "无法获取相应的configCenter";
			logger.error(errMsg);
			throw new RuntimeException(errMsg);
		}
		
		
		configProvider.setConfigCenter(configCenter);
		this.providers.add(configProvider);
		
		return true;
	}


	/**
	 * 从注册进来的provider中获取配置，构建
	 */
	public boolean build() throws Exception {
		
		/**
		 * 是否是开发模式
		 */
		boolean isDevMode = true;
		
		/**
		 * 将configProvider的数据注册给configCenter
		 */
		logger.debug("开始将configProvider的数据注册给configCenter");
		logger.debug("当前模式：isDevMode = "+isDevMode);
		if(this.providers != null){
			for(ConfigProvider configProvider: this.providers){
				
				/**非开发模式时，DEV 类型的provider不需要注册数据*/
				if(!isDevMode && configProvider.getConfigCenter().getPriority() == CenterPriorityEnum.DEV.getPriority()){
					logger.debug(configProvider.getClass()+"不提供数据");
					continue;
				}
				
				configProvider.registerToCenter();
			}
		}
		logger.debug("configManager构造完成");
		return true;
	}


	

	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.temp.ConfigManager#injectConfig(java.lang.Object)
	 */
	@Override
	public boolean injectConfig(Object t) {
		// TODO Auto-generated method stub
		return false;
	}


	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.temp.ConfigManager#injectConfig(java.lang.Class)
	 */
	@Override
	public Object injectConfig(Class clazz) {
		// TODO Auto-generated method stub
		return null;
	}
}
