package com.zengshi.paas.mail.impl;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.zengshi.paas.ConfigurationCenter;
import com.zengshi.paas.ConfigurationWatcher;
import com.zengshi.paas.PaasException;
import com.zengshi.paas.mail.IMailManager;
import com.zengshi.paas.utils.JSONValidator;

public class MailManager implements IMailManager, ConfigurationWatcher {
	private static transient Logger log = Logger.getLogger(MailManager.class);
	private ConfigurationCenter confCenter = null;

	private String confPath = "/com/zengshi/paas/mail/conf";
	private JavaMailSenderImpl mailSender = null;

	private final String CHARSET_UTF = "UTF-8";

	private String mailSmtpHost = "";
	private String mailSmtpPort = "25";
	private boolean mailSmtpAuth = false;
	private String mailSmtpAcct = "";
	private String mailSmtpAcctPasswd = "";
	private String mailContentType = "";

	private final String KEY_SMTP_HOST = "smtp.host";
	private final String KEY_SMTP_PORT = "smtp.port";
	private final String KEY_SMTP_AUTH = "smtp.auth";
	private final String KEY_SMTP_ACCT = "smtp.acct";
	private final String KEY_SMTP_ACCT_PASSSWD = "smtp.acct.passwd";
	private final String KEY_MAIL_TYPE = "mail.type";

	public MailManager() {
	}

	public void init() {
		try {
			process(confCenter.getConfAndWatch(confPath, this));
		} catch (PaasException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void process(String conf) {
		if (log.isInfoEnabled()) {
			log.info("new log configuration is received: " + conf);
		}
		try {
			JSONObject json = JSONObject.fromObject(conf);
			boolean changed = false;
			if (JSONValidator.isChanged(json, KEY_SMTP_HOST, mailSmtpHost)) {
				changed = true;
				mailSmtpHost = json.getString(KEY_SMTP_HOST);
			}
			if (JSONValidator.isChanged(json, KEY_SMTP_PORT, mailSmtpPort)) {
				changed = true;
				mailSmtpPort = json.getString(KEY_SMTP_PORT);
			}
			if (JSONValidator.isChanged(json, KEY_SMTP_AUTH, "" + mailSmtpAuth)) {
				changed = true;
				mailSmtpAuth = json.getBoolean(KEY_SMTP_AUTH);
			}
			if (JSONValidator.isChanged(json, KEY_SMTP_ACCT, mailSmtpAcct)) {
				// changed = true;
				mailSmtpAcct = json.getString(KEY_SMTP_ACCT);
			}
			if (JSONValidator.isChanged(json, KEY_SMTP_ACCT_PASSSWD,
					mailSmtpAcctPasswd)) {
				// changed = true;
				mailSmtpAcctPasswd = json.getString(KEY_SMTP_ACCT_PASSSWD);
			}
			if (JSONValidator.isChanged(json, KEY_MAIL_TYPE, mailContentType)) {
				// changed = true;
				mailContentType = json.getString(KEY_MAIL_TYPE);
			}
			if (changed) {
				mailSender.setHost(mailSmtpHost);
				mailSender.setPort(Integer.parseInt(mailSmtpPort));
				Properties props = new Properties();
				props.put("mail.smtp.auth", mailSmtpAuth);
				mailSender.setJavaMailProperties(props);
				mailSender.setUsername(mailSmtpAcct);
				mailSender.setPassword(mailSmtpAcctPasswd);

				if (log.isInfoEnabled()) {
					log.info("mail server address is changed to " + mailSender);
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}

	@Override
	public void sendMail(String to, String title, String content)
			throws PaasException {
		try {
			MimeMessage mime = mailSender.createMimeMessage();
			MimeMessageHelper mimehelper;
			mimehelper = new MimeMessageHelper(mime, true, CHARSET_UTF);
			mimehelper.setFrom(mailSmtpAcct);// 设置发送人
			mimehelper.setTo(to);// 设置收件人
			mimehelper.setSentDate(new Date());// 设置发送日期
			mimehelper.setSubject(title);// 设置主题
			mimehelper.setText(content, true);// 设置邮件内容为HTML超文本格式
			mailSender.send(mime);// 将邮件发送
		} catch (MessagingException e) {
			log.error("Send mail error", e);
			throw new PaasException("Paas-mail-1001","Send mail error!", e);
		}
	}

	@Override
	public void sendMail(String from, String to, String title, String content)
			throws PaasException {
		try {
			MimeMessage mime = mailSender.createMimeMessage();
			MimeMessageHelper mimehelper;
			mimehelper = new MimeMessageHelper(mime, true, CHARSET_UTF);
			mimehelper.setFrom(from);// 设置发送人
			mimehelper.setTo(to);// 设置收件人
			mimehelper.setSentDate(new Date());// 设置发送日期
			mimehelper.setSubject(title);// 设置主题
			mimehelper.setText(content, true);// 设置邮件内容为HTML超文本格式
			mailSender.send(mime);// 将邮件发送
		} catch (MessagingException e) {
			log.error("Send mail error", e);
			throw new PaasException("Paas-mail-1001","Send mail error!", e);
		}
	}

	@Override
	public void sendMail(List<String> toList, String title, String content)
			throws PaasException {
		try {
			MimeMessage mime = mailSender.createMimeMessage();
			MimeMessageHelper mimehelper;
			mimehelper = new MimeMessageHelper(mime, true, CHARSET_UTF);
			mimehelper.setFrom(mailSmtpAcct);// 设置发送人
			mimehelper.setTo(toList.toArray(new String[toList.size()]));// 设置收件人
			mimehelper.setSentDate(new Date());// 设置发送日期
			mimehelper.setSubject(title);// 设置主题
			mimehelper.setText(content, true);// 设置邮件内容为HTML超文本格式
			mailSender.send(mime);// 将邮件发送
		} catch (MessagingException e) {
			log.error("Send mail error", e);
			throw new PaasException("Paas-mail-1001","Send mail error!", e);
		}
	}

	@Override
	public void sendMail(String from, List<String> toList, String title,
			String content) throws PaasException {
		try {
			MimeMessage mime = mailSender.createMimeMessage();
			MimeMessageHelper mimehelper;
			mimehelper = new MimeMessageHelper(mime, true, CHARSET_UTF);
			mimehelper.setFrom(from);// 设置发送人
			mimehelper.setTo(toList.toArray(new String[toList.size()]));// 设置收件人
			mimehelper.setSentDate(new Date());// 设置发送日期
			mimehelper.setSubject(title);// 设置主题
			mimehelper.setText(content, true);// 设置邮件内容为HTML超文本格式
			mailSender.send(mime);// 将邮件发送
		} catch (MessagingException e) {
			log.error("Send mail error", e);
			throw new PaasException("Paas-mail-1001","Send mail error!", e);
		}
	}

	public ConfigurationCenter getConfCenter() {
		return confCenter;
	}

	public void setConfCenter(ConfigurationCenter confCenter) {
		this.confCenter = confCenter;
	}

	public String getConfPath() {
		return confPath;
	}

	public void setConfPath(String confPath) {
		this.confPath = confPath;
	}

	public JavaMailSenderImpl getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSenderImpl mailSender) {
		this.mailSender = mailSender;
	}

}
