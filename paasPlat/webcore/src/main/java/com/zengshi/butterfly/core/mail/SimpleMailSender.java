package com.zengshi.butterfly.core.mail;

import java.util.Date;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.zengshi.butterfly.core.config.Constants;
import com.zengshi.butterfly.core.security.encrypt.DESEncrypt;

class MailAuthenticator extends Authenticator {
	String userName = null;
	String password = null;

	public MailAuthenticator() {
	}

	public MailAuthenticator(String username, String password) {
		this.userName = username;
		this.password = password;
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(userName, password);
	}
}

public class SimpleMailSender {
	private MailServerInfo mailInfo;

	public MailServerInfo getMailInfo() {
		return mailInfo;
	}

	public void setMailInfo(MailServerInfo mailInfo) {
		this.mailInfo = mailInfo;
	}

	/**
	 * 默认构造函数直接从数据库里面获取默认配置
	 */
	public SimpleMailSender() {
		this.mailInfo = new MailServerInfo();
		this.mailInfo.setMailServerHost(Constants.MAIL_HOST.getValue());
		this.mailInfo.setMailServerPort(Constants.MAIL_PORT.getValue());
		this.mailInfo.setUserName(Constants.MAIL_USERNAME.getValue());
		this.mailInfo.setPassword(DESEncrypt.decode(Constants.MAIL_PASSWD.getValue()));
		this.mailInfo.setValidate("true"
				.equalsIgnoreCase(Constants.MAIL_VALIDATE.getValue()) ? true
				: false);
		this.mailInfo.setFromAddress(Constants.MAIL_FROMADDRESS.getValue());
	}

	/**
	 * 
	 * @param mailInfo
	 *            MailServerInfo配置对象
	 */
	public SimpleMailSender(MailServerInfo mailInfo) {
		super();
		this.mailInfo = mailInfo;
	}

	/**
	 * 发送textmail
	 * 
	 * @param toAddress
	 *            收件地址
	 * @param subject
	 *            邮件主题
	 * @param content
	 *            邮件内容
	 * 
	 */
	public boolean sendTextMail(String toAddress, String subject, String content) {
		return sendMail(toAddress, subject, content, "text");
	}

	/**
	 * 发送html邮件
	 * 
	 * @param toAddress
	 *            收件地址
	 * @param subject
	 *            邮件主题
	 * @param content
	 *            邮件内容
	 * 
	 */
	public boolean sendHtmlMail(String toAddress, String subject, String content) {
		return sendMail(toAddress, subject, content, "html");
	}

	/**
	 * 
	 * @param toAddress
	 * @param subject
	 * @param content
	 * @param mailType
	 *            0:text;1:html
	 * @return
	 */
	private boolean sendMail(String toAddress, String subject, String content,
			String mailType) {

		MailAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();

		if (mailInfo.isValidate()) {
			authenticator = new MailAuthenticator(mailInfo.getUserName(),
					mailInfo.getPassword());
		}
		Session sendMailSession = Session
				.getDefaultInstance(pro, authenticator);
		try {
			Message mailMessage = new MimeMessage(sendMailSession);
			Address from = new InternetAddress(mailInfo.getFromAddress());
			mailMessage.setFrom(from);
			Address to = new InternetAddress(toAddress);
			mailMessage.setRecipient(Message.RecipientType.TO, to);
			mailMessage.setSubject(subject);
			mailMessage.setSentDate(new Date());
			if ("html".equalsIgnoreCase(mailType)) {
				Multipart mainPart = new MimeMultipart();
				BodyPart html = new MimeBodyPart();
				html.setContent(content, "text/html; charset=utf-8");
				mainPart.addBodyPart(html);
				mailMessage.setContent(mainPart);
			} else {
				mailMessage.setText(content);
			}
			Transport.send(mailMessage);
			return true;
		} catch (MessagingException ex) {
			ex.printStackTrace();
		}
		return false;
	}

}
