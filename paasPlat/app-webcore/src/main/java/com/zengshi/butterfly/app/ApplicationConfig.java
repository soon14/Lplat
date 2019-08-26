/**
 * 
 */
package com.zengshi.butterfly.app;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ApplicationConfig {

	private static Map<String,ActionConfig> actionConfig=new HashMap<String, ActionConfig>();
	
	public static void addActionConfig(String bizCode,ActionConfig config) {
		actionConfig.put(bizCode, config);
	}
	
	public static ActionConfig getActionConfig(String bizCode) {
		return actionConfig.get(bizCode);
	}
	
	
}
