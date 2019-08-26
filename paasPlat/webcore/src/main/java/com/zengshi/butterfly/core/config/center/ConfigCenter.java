/**
 * 
 */
package com.zengshi.butterfly.core.config.center;

import java.util.Map;

import com.zengshi.butterfly.core.config.Config;

/**
 * 配置中心接口
 * 2011-7-18
 */
public interface ConfigCenter {
	
	/**
	 * 判断该配置中心是否开放，只有开放的配置中心才可以获取配置数据
	 * @return
	 */
	public boolean isOpen();
	
	/**
	 * 根据配置的key值，从当前配置中心获取相应的配置
	 * @param key
	 * @return
	 */
	public Config getConfig(String key);
	
	/**
	 * 获取当前配置中心的优先级
	 * @return 
	 */
	public int getPriority();
	
	/**
	 * 往配置中心添加多条配置
	 * @param configs Map类型
	 */
	public void addConfigs(Map<String,Config> configs);
	
	/**往配置中心添加一条配置
	 * @param config
	 */
	public void addConfig(Config config);
	
	/**
	 * 删除配置中心的一条配置
	 * @param key
	 */
	public void removeConfig(String key);
}
