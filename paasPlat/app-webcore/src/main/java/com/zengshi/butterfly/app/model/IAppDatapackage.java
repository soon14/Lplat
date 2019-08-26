/**
 * 
 */
package com.zengshi.butterfly.app.model;

import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * 
 * 增加setHeader 方法，在Mapp中内部调用接口时，生成新的请求包头时使用
 * @modify mler
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public interface IAppDatapackage {

	public IHeader getHeader();

	public void setHeader(IHeader header);

	public IBody getBody();

	public void setBody(IBody body);
	
	
	
}
