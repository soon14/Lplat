/**
 * 
 */
package com.zengshi.butterfly.core.config;

/**
 * 一条配置记录
 * 2011-7-22
 */
public interface Config<T> {
	/**
	 * 获取配置key
	 * @return
	 */
	public String getKey();
	/**
	 * 获取配置值
	 * @return
	 */
	public T getValue();
	
}
