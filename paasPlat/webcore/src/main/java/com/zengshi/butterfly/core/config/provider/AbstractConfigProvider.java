/**
 * 
 */
package com.zengshi.butterfly.core.config.provider;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.zengshi.butterfly.core.config.Config;
import com.zengshi.butterfly.core.config.ConfigContainer;
import com.zengshi.butterfly.core.config.center.CenterPriorityEnum;
import com.zengshi.butterfly.core.config.center.ConfigCenter;


public abstract class AbstractConfigProvider implements ConfigProvider{
	
	/**
	 * 配置中心
	 */
	protected ConfigCenter configCenter ;
	/**
	 * 配置信息
	 */
	protected Map<String,Config> configs = new HashMap<String,Config>();
	/**
	 * 是否被初始化过
	 */
	protected boolean isInited = false;
	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.temp.ConfigProvider#init(com.zengshi.cu.config.temp.ConfigContainer, int)
	 */
	@Override
	public boolean init(ConfigContainer configContainer, CenterPriorityEnum centerPriority) {
		if(configContainer == null){
			this.getLogger().error(this.getClass().toString()+"实例初始化失败，configContainer参数不能为空！");
			throw new RuntimeException("configContainer is null");
		}
		this.configCenter = configContainer.findConfigCenter(centerPriority);
		if(this.configCenter == null){
			this.getLogger().error(this.getClass().toString()+"实例初始化失败，无法从configContainer获取configCenter！");
			throw new RuntimeException("can‘t find configCenter in configContainer");
		}
		
		if(isInited){
			this.getLogger().warn("当前configProvider已经被初始化，无法再次初始化");
			return false;
		}
		
		//cleanConfigs();
		//loadConfigs();
		this.isInited = true;
		
		return true;
	}
	
	
	
	protected void cleanConfigs(){
		this.configs = new HashMap<String,Config>();
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.temp.ConfigProvider#reload()
	 */
	@Override
	public boolean reload() {
		cleanConfigs();
		
		return true;
	}
	

	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.provider.ConfigProvider#registerToCenter(com.zengshi.cu.config.center.CenterPriorityEnum)
	 */
	@Override
	public boolean registerToCenter() {
		if(this.configCenter == null){
			this.getLogger().error("当前configProvider没有关联configCenter, 无法注册配置数据");
			return false;
		}
		
		
		try {
			loadConfigs();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.getLogger().debug(this.getClass()+" 将数据注册给中心"+this.configCenter.getClass());
		this.configCenter.addConfigs(this.configs);
		
		return true;
	}

	
	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.provider.ConfigProvider#getConfigVenter()
	 */
	@Override
	public ConfigCenter getConfigCenter() throws Exception {
		return this.configCenter;
	}
	
	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.provider.ConfigProvider#setConfigCenter()
	 */
	public void setConfigCenter(ConfigCenter configCenter)throws Exception{
		this.configCenter = configCenter;
	}
	
	
	
	/**
	 * 从特定的数据源加载配置数据
	 */
	protected abstract void loadConfigs() throws Exception;
	
	protected void addConfig(Config config){
		this.configs.put(config.getKey(), config);
	}
	
	public abstract Log getLogger();
}
