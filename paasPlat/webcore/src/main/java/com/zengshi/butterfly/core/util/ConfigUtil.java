package com.zengshi.butterfly.core.util;

import com.zengshi.butterfly.core.config.Config;
import com.zengshi.butterfly.core.config.ConfigManager;
import com.zengshi.butterfly.core.config.ConfigManagerDefault;
import com.zengshi.butterfly.core.config.center.CenterPriorityEnum;
import com.zengshi.butterfly.core.config.provider.PropertyConfigProvider;
import com.zengshi.butterfly.core.config.provider.XMLConfigProvider;

public class ConfigUtil {
	
	private static ConfigManager configManager ;

	private static void initConfigManager() {
		//默认提供的配置信息提供者
		try {
			String configmanager=System.getProperty("application.config.manager.class");
			if(configmanager != null && !"".equals(configmanager.trim())) {
				//TODO yangqx 配置管理器
				//Class configmanagerClazz=Class.forName(configmanager);
				//configmanager=Class.forName(configmanager)
			}else {
				configManager=ConfigManagerDefault.getInstance();
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过配置key获取Config
	 * @param key
	 * @return
	 */
	public static String getValue(String key){
		try {
			if(configManager == null) initConfigManager();
			Config config = configManager.getConfigContainer().findConfig(key);
			return config == null? null:config.getValue().toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * 通过配置key获取Config
	 * @param key
	 * @return
	 */
	public static Config getConfig(String key){
		try {
			if(configManager == null) initConfigManager();
			return configManager.getConfigContainer().findConfig(key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	/**
	 * 通过配置key, 和指定的 centerPriority 获取Config
	 * @param key
	 * @param centerPriority
	 * @return
	 */
	public static Config getConfig(String key, CenterPriorityEnum centerPriority){
		try {
			if(configManager == null) initConfigManager();
			return configManager.getConfigContainer().findConfig(key,centerPriority);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String args[]){
		System.out.println(ConfigUtil.getValue("config2.level2.level3.level4"));
	}
}
