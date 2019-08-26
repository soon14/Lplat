package com.zengshi.butterfly.core.config;

import com.zengshi.butterfly.core.config.center.CenterPriorityEnum;
import com.zengshi.butterfly.core.config.center.ConfigCenter;

/**
 * 配置容器
 * 2011-7-18
 */
public interface ConfigContainer extends Injectable {

	/**
	 * 根据key值，依据配置中心的优先等级，查找对应的配置
	 * @param key
	 * @return
	 */
	public Config findConfig(String key);
	/**
	 * 根据配置中心优先等级描述枚举对象 来查找对应的配置中心
	 * @param centerPriority
	 * @return
	 */
	public ConfigCenter findConfigCenter(CenterPriorityEnum centerPriority);
	
	/**
	 * 添加配置中心
	 * @param conifgCenter
	 * @return
	 */
	public boolean addConfigCenter(ConfigCenter conifgCenter);
	
	/**
	 * 重置所有配置中心的值
	 * @return
	 */
	public void reset();
	
	/**
	 * 重置指定优先级的配置中心的值
	 * @param centerPriority
	 * @return
	 */
	public void reset(CenterPriorityEnum centerPriority);
	
	/**
	 * 清除配置中心的值
	 * @return
	 */
	public void clear();
	
	/**
	 * 给指定优先级的配置中心，添加一条配置
	 * @param centerPriority
	 * @param config
	 * @return
	 */
	public void addConfig(CenterPriorityEnum centerPriority, Config config);
	
	/**
	 * 从指定优先级的配置中心，删除一条配置
	 * @param centerPriority
	 * @param config
	 * @return
	 */
	public void removeConfig(CenterPriorityEnum centerPriority, Config config);
	
	/**
	 * 从指定优先级的配置中心查找对应的配置
	 * @param key
	 * @param centerPriority
	 * @return
	 */
	public Config findConfig(String key, CenterPriorityEnum centerPriority);

}
