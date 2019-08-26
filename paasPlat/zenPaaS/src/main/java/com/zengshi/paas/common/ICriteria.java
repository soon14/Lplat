package com.zengshi.paas.common;

/**
 * 
 * 修改记录
 * 2016.02.02
 * 新增setOrderByClause接口
 */
public interface ICriteria {
	/**
	 * 重写setOrderByClause，用于接口部分可以处理表格排序列名称及排序方式
	 * 用于BaseInfo、BasePageVO可以获得排序列参数进行处理
	 * @param orderByClause
	 */
	 void setOrderByClause(String orderByClause);
}
