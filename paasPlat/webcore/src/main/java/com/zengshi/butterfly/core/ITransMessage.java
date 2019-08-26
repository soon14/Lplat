package com.zengshi.butterfly.core;

/**
 * 负责数据的输入和输出
 *
 * @param <R>
 * @param <S>
 */
public interface ITransMessage<R> {

	/**
	 * 设置输入数据
	 * @param r
	 */
	public void setRequest(R r);
	
	/**
	 * 获取请求对象
	 * @return
	 */
	public R getRequest();
	
}
