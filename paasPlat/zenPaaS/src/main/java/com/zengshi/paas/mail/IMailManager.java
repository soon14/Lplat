package com.zengshi.paas.mail;

import java.util.List;

import com.zengshi.paas.PaasException;

public interface IMailManager {

	/**
	 * 为指定的人发送邮件
	 * 
	 * @param to
	 * @param title
	 * @param content
	 * @throws PaasException
	 */
	public void sendMail(String to, String title, String content)
			throws PaasException;

	public void sendMail(String from, String to, String title, String content)
			throws PaasException;

	public void sendMail(List<String> toList, String title, String content)
			throws PaasException;

	public void sendMail(String from, List<String> toList, String title,
			String content) throws PaasException;

}
