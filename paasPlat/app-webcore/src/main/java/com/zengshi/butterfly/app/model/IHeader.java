package com.zengshi.butterfly.app.model;

import org.codehaus.jackson.annotate.JsonTypeInfo;

import javax.xml.bind.annotation.XmlSeeAlso;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
@XmlSeeAlso({AppHeader.class})
public interface IHeader  {

	public String getBizCode();
	
	public String getIdentityId();
	
	public void setRespCode(String respCode);
	
	public void setRespMsg(String respMsg);
	
	public String getRespCode();
	
	public String getRespMsg();
	
	public String getSign();
	
	public String getMode();
	
	public void setSign(String sign);
	
}
