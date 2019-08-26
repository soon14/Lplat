package com.zengshi.paas.mail.impl;

import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONObject;

import com.zengshi.paas.AbstractConfigurationWatcher;
import com.zengshi.paas.ConfigurationWatcher;
import com.zengshi.paas.PaasException;
import com.zengshi.paas.mail.IMailManager;
import com.zengshi.paas.mail.MailMessage;
import com.zengshi.paas.message.MessageSender;
import com.zengshi.paas.utils.JSONValidator;
/**
 * 负责延迟发送邮件的管理类
 *
 */
public class DelayMailManager extends AbstractConfigurationWatcher implements IMailManager, ConfigurationWatcher {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2547993985048718088L;
	private MessageSender sender;
	private String confPath = "/com/zengshi/paas/storm/mail/conf";
	private String topic;

	@SuppressWarnings("unchecked")
	public void sendMail(String to, String title, String content) throws PaasException {
		MailMessage mailMessage = new MailMessage();
		mailMessage.setToList(Arrays.asList(new String[]{to}));
		mailMessage.setTitle(title);
		mailMessage.setContent(content);
		sender.sendMessage(mailMessage.toJsonString(), topic);
	}

	@SuppressWarnings("unchecked")
	public void sendMail(String from, String to, String title, String content) throws PaasException {
		MailMessage mailMessage = new MailMessage();
		mailMessage.setFrom(from);
		mailMessage.setToList(Arrays.asList(new String[] { to }));
		mailMessage.setTitle(title);
		mailMessage.setContent(content);
		sender.sendMessage(mailMessage.toJsonString(), topic);
	}

	public void sendMail(List<String> toList, String title, String content) throws PaasException {
		MailMessage mailMessage = new MailMessage();
		mailMessage.setToList(toList);
		mailMessage.setTitle(title);
		mailMessage.setContent(content);
		sender.sendMessage(mailMessage, topic);
	}

	public void sendMail(String from, List<String> toList, String title, String content) throws PaasException {
		MailMessage mailMessage = new MailMessage();
		mailMessage.setFrom(from);
		mailMessage.setToList(toList);
		mailMessage.setTitle(title);
		mailMessage.setContent(content);
		sender.sendMessage(mailMessage.toJsonString(), topic);
	}

	public void process(String conf) {
		JSONObject json = JSONObject.fromObject(conf);
		if (JSONValidator.isChanged(json, "kafka.topic", topic)) {
			topic = json.getString("kafka.topic");
		}
	}

	public void setSender(MessageSender sender) {
		this.sender = sender;
	}

	public void setConfPath(String confPath) {
		this.confPath = confPath;
	}

	public String getConfPath() {
		return confPath;
	}

}
