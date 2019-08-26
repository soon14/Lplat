/**
 * 
 */
package com.zengshi.butterfly.core.config.center;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.zengshi.butterfly.core.config.Config;

/**
 * 配置中心抽象实现
 * 2011-7-14
 */
public abstract class AbstractConfigCenter implements ConfigCenter{
	
	
	/**
	 * 配置中心优先级别
	 */
	protected int priority ;
	/**
	 * 当前配置中心的所有配置
	 */
	protected Map<String,Config> configsInThisCenter = new HashMap<String,Config>();
	
	
	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.center.ConfigCenter#isOpen()
	 */
	public boolean isOpen(){
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.center.ConfigCenter#getConfig(java.lang.String)
	 */
	public Config getConfig(String key){
		
		
		if( key != null && !"".equals(key.trim())){
			return this.configsInThisCenter.get(key);
		}else{
			return null;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.center.ConfigCenter#addConfigs(java.util.Map)
	 */
	public void addConfigs(Map<String,Config> configs){
		if(this.configsInThisCenter == null){
			this.configsInThisCenter = new HashMap<String,Config>();
		}
		
		if(configs == null || configs.isEmpty()){
			return;
		}
		
		Iterator<String> iterator = configs.keySet().iterator();
		while(iterator.hasNext()){
			addConfig(configs.get(iterator.next()));
		}
	}
	
	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.center.ConfigCenter#addConfig(com.zengshi.cu.config.temp.Config)
	 */
	public void addConfig(Config config){
		if(config == null){
			return;
		}
		
		if(this.configsInThisCenter==null){
			this.configsInThisCenter = new HashMap<String, Config>();
		}
		Log logger = getLogger();
		String key = config.getKey();
		if(configsInThisCenter.containsKey(key)){
			logger.warn("存在冲突的key值: "+key+"，请修改");
		}
		configsInThisCenter.put(key, config);
	}
	
	public void removeConfig(String key){
		if(key == null ){
			return ;
		}
		this.configsInThisCenter.remove(key);
	}
	
	
	/**
	 * 获取日志对象
	 * @return
	 */
	protected abstract Log getLogger();
	
}
