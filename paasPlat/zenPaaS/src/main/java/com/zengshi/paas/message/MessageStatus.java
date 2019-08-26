package com.zengshi.paas.message;

/**
 * 消息服务-消息状态类
 *
 */
public class MessageStatus {
	private short flag = 1;
	
	public boolean isRollbackOnly() {
		if(flag == 0) {
			return true;
		}else {
			return false;
		}
	}
	
	public void setRollbackOnly() {
		flag = 0;
	}
}
