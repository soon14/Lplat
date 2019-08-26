/**
 * 
 */
package com.zengshi.butterfly.core.config.center;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置中心工厂<br>
 * 统一类型的配置中心，只能获取一个相同的实例
 * 2011-7-18
 */
public class ConfigCenterFactory {
	
	private static List<ConfigCenter> uniqueCenterInstances = new ArrayList<ConfigCenter>();
	
	/**
	 * 获取中心实例</br>
	 * 1. 如果之前已经通过factory获取过相同优先级的中心实例，则获取原有对象实例</br>
	 * 2. 否则创建一个新的实例</br>
	 * @param configPriority 中心优先级枚举对象
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static  ConfigCenter getConfigCenter(CenterPriorityEnum configPriority) throws InstantiationException, IllegalAccessException{
		
		/**
		 * 如果已经存在相同优先级的中心实例，则获取原有对象
		 */
		for(ConfigCenter configCenter : uniqueCenterInstances){
			if(configCenter.getPriority() == configPriority.getPriority()){
				return configCenter;
			}
		}
		
		/**
		 * 否则创建一个新的实例
		 */
		ConfigCenter configCenter = (ConfigCenter) configPriority.getCenterClass().newInstance();
		
		uniqueCenterInstances.add(configCenter);
		return configCenter;
	}
}
