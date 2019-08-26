package com.zengshi.paas.message;


/**
 * 消息服务-消息监听接口类
 *
 */
public interface MessageListener {
	public void receiveMessage(Message message, MessageStatus status);
}
