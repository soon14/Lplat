/**
 * 
 */
package com.zengshi.butterfly.core.config;

/**
 * 2011-7-22
 */
public class DefaultConfig extends AbstractConfig {
	
	private final String KEY = "CONFIG_KEY";
	private final String VALUE = "CONFIG_VALUE";
	
	public DefaultConfig(String key,Object value){
		this.attr.put(KEY, key);
		this.attr.put(VALUE, value);
	}
	
	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.temp.Config#getKey()
	 */
	public String getKey(){
		Object configkey = this.attr.get(KEY) ;
		return  configkey == null? null: (String)configkey;
	}

	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.temp.Config#getValue()
	 */
	public Object getValue() {
		return this.attr.get(VALUE);
	}
	
	public String toString(){
		Object configValue = this.attr.get(VALUE) ;
		return  configValue == null? null: configValue.toString();
	}
}
