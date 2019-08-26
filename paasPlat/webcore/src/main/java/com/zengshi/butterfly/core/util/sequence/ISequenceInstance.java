/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zengshi.butterfly.core.util.sequence;

/**
 * 
 */
public interface ISequenceInstance {
	/**
	 * 获取主键
	 * 
	 * @param name
	 *            主键名
	 * @return 主键
	 */
	public Long getNext(String name);

	/**
	 * 每次获取pertimes个主键
	 * 
	 * @param name
	 *            主键名
	 * @param pertimes
	 * @return 开始值
	 */
	public Long getNext(String name, int pertimes);

	/**
	 * 设置主键值
	 * 
	 * @param name
	 *            主键名
	 * @param value
	 *            主键值
	 */
	public void setValue(String name, Long value);
}
