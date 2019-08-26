package com.zengshi.butterfly.core.mail.example;

import com.zengshi.butterfly.core.mail.MailServerInfo;
import com.zengshi.butterfly.core.mail.SimpleMailSender;

public class MailExample {
	public static void main(String[] args) {
		MailServerInfo mailInfo = new MailServerInfo();
		mailInfo.setMailServerHost("mail.izengshi.com");
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);
		mailInfo.setUserName("panght");
		mailInfo.setPassword("pht789!!");
		mailInfo.setFromAddress("panght@izengshi.com");

		SimpleMailSender sms = new SimpleMailSender(mailInfo);
		/**
		 * 也可以调用默认的构造函数读取默认配置 SimpleMailSender sms = new SimpleMailSender();
		 */
		sms.sendTextMail("panght@izengshi.com", "邮箱主题", "邮箱内容");
		sms.sendHtmlMail("panght@izengshi.com", "邮箱主题", "邮箱内容");
	}
}
