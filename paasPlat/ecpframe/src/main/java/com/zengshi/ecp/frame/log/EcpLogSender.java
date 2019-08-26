/**
 * 
 */
package com.zengshi.ecp.frame.log;

import com.zengshi.paas.message.MessageSender;

/**
 *
 */
public class EcpLogSender {
	
	private MessageSender logMessageSender;
	private String logTopic;
	public EcpLogSender(MessageSender sender, String logTopic){
		
		this.logMessageSender = sender;
		
		this.logTopic = logTopic;
	}
	
	///������־��
	public void sendLog(String message){
		
		this.logMessageSender.sendMessage(message, this.logTopic);
	}
	
	
}
