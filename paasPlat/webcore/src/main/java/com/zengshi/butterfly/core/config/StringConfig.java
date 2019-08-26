/**
 * 
 */
package com.zengshi.butterfly.core.config;

/**
 * 2011-7-15
 */
public class StringConfig implements Config<String> {

	private String key;
	private String value;
	public StringConfig(String key,String value) {
		this.key=key;
		this.value=value;
	}
	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return this.key;
	}

	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return value;
	}

	
	
}
