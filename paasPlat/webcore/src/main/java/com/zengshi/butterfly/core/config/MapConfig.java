/**
 * 
 */
package com.zengshi.butterfly.core.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 2011-7-22
 */
public class MapConfig extends DefaultConfig {

	/**
	 * @param key
	 * @param value
	 */
	public MapConfig(String key, Map value) {
		super(key, value);
	}
	
//	public void put(String key,String value){
//		this.attr.put(key, value);
//		
//		Object configValue = this.getValue();
//		if(configValue != null){
//			((Map)configValue).put(key, value);
//		}
//	}
	
	
	@Override
	public Map getValue(){
		Object obj = super.getValue();
		if(obj == null){
			return null;
		}else{
			Map<String,Object> clonedMap = new HashMap<String,Object>();
			clonedMap.putAll((Map) obj);
			return clonedMap;
		}
	}
	
	
	public static void main(String args[]){
		Config config = new MapConfig("myKey",null);
		System.out.println(config.getKey());
		
	}

}
