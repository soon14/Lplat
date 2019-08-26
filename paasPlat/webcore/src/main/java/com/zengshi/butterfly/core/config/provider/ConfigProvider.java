package com.zengshi.butterfly.core.config.provider;

import com.zengshi.butterfly.core.config.ConfigContainer;
import com.zengshi.butterfly.core.config.center.CenterPriorityEnum;
import com.zengshi.butterfly.core.config.center.ConfigCenter;

/**
 * 配置信息提供者
 * 2011-7-18
 */
public interface ConfigProvider {
	
	/**
	 * 初始化配置信息提供者<br>
//	 * 1. 根据centerPriority 从configContainer获取对应的configCenter 与当前provider关联<br>
	 * 2. 别的初始化动作
	 * @param configContainer
	 * @param centerPriority
	 * @return
	 */
	public boolean init(ConfigContainer configContainer, CenterPriorityEnum centerPriority) throws Exception;
	
	/**
	 * 从特定的数据源，重新加载提供者能提供的配置信息
	 * @return
	 */
	public boolean reload()throws Exception;
	
	/**
	 * 将配置信息提供者注册给配置中心，<br>
	 * 1. 加载配置数据<br>
	 * 2. 将配置数据注册给 configCenter。ps：做一个推送的过程
	 * @param configCenter
	 * @return
	 */
	public boolean registerToCenter()throws Exception;
	
	/**
	 * 返回当前provider关联的configCenter
	 * @return
	 * @throws Exception
	 */
	public ConfigCenter getConfigCenter()throws Exception;
	
	/**
	 * 设置configCenter
	 * @throws Exception
	 */
	public void setConfigCenter(ConfigCenter configCenter)throws Exception;
	
	
}
