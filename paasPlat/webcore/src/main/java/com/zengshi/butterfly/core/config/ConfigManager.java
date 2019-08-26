/**
 * 
 */
package com.zengshi.butterfly.core.config;

import com.zengshi.butterfly.core.config.center.CenterPriorityEnum;
import com.zengshi.butterfly.core.config.provider.ConfigProvider;

/**
 * 配置信息管理器
 * 2011-7-18
 * @param <T>
 */
public interface ConfigManager {
	/** 
	 * 获取配置信息容器实例 
	 * @throws Exception 
	 */
	public ConfigContainer getConfigContainer()throws Exception;
	
	/**
	 * 添加配置信息提供者<br>
	 * 该provider需已经关联的配置中心<br>
	 * @param provider
	 * @return
	 * @throws Exception 
	 */
	public boolean addProvider(ConfigProvider provider) throws Exception;

	/**
	 * 添加配置信息提供者
	 * @param configProvider 配置信息提供者
	 * @param centerPriority 配置中心优先级别
	 * @throws Exception 
	 */
	public boolean addProvider(ConfigProvider configProvider, CenterPriorityEnum CenterPriority)throws Exception;

	/**
	 * 构建配置 
	 * @throws Exception 
	 */
	public boolean build()throws Exception;

	public <T> boolean injectConfig(T Object)throws Exception;

	public <T> T injectConfig(Class<T> clazz)throws Exception;

}
