package com.zengshi.butterfly.core.examples.config;

import com.zengshi.butterfly.core.config.CfgInject;

public class UserInfo {

	
	private String userName;

	public String getUserName() {
		return userName;
	}

	@CfgInject(key="username")
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
}
