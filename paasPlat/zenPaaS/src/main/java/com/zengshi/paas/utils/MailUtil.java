package com.zengshi.paas.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.zengshi.paas.PaasException;
import com.zengshi.paas.mail.IMailManager;

public class MailUtil {
	private static final Logger log = Logger.getLogger(MailUtil.class);
	private static IMailManager mailSv = null;
	private static IMailManager delayMailSv = null;

	private MailUtil() {

	}

	private static IMailManager getIntance() {
		if (null != mailSv)
			return mailSv;
		else {
			mailSv = (IMailManager) PaasContextHolder.getContext().getBean("mailSv");
			return mailSv;
		}
	}

	private static IMailManager getDelayInstance() {
		if (null != delayMailSv)
			return delayMailSv;
		else {
			delayMailSv = (IMailManager) PaasContextHolder.getContext().getBean("delayMailSv");
			return delayMailSv;
		}
	}

	public static void sendMail(String to, String title, String content) throws PaasException {
		getIntance().sendMail(to, title, content);
	}

	public static void sendMail(String from, String to, String title, String content) throws PaasException {
		getIntance().sendMail(from, to, title, content);
	}

	public static void sendMail(List<String> toList, String title, String content) throws PaasException {
		getIntance().sendMail(toList, title, content);
	}

	public static void sendMail(String from, List<String> toList, String title, String content) throws PaasException {
		getIntance().sendMail(from, toList, title, content);
	}

	public static void sendDelayMail(String to, String title, String content) throws PaasException {
		getDelayInstance().sendMail(to, title, content);
	}

	public static void sendDelayMail(String from, String to, String title, String content) throws PaasException {
		getDelayInstance().sendMail(from, to, title, content);
	}

	public static void sendDelayMail(List<String> toList, String title, String content) throws PaasException {
		getDelayInstance().sendMail(toList, title, content);
	}

	public static void sendDelayMail(String from, List<String> toList, String title, String content) throws PaasException {
		getDelayInstance().sendMail(from, toList, title, content);
	}

	public static void main(String[] args) {
		try {
			// MailUtil.sendMail("douxf@izengshi.com", "测试邮件",
			// "撒的撒</br><h1>萨达</h1>");
			// MailUtil.sendMail("douxf@izengshi.com","douxf@izengshi.com",
			// "测试邮件",
			// "撒的撒</br><h1>萨达</h1>")
			int i = 0;
			while (i < 100) {
				log.info("sssssss----------测试中文是否好的--------");
				i++;
			}
			List<String> tos = new ArrayList<String>();
			tos.add("douxf@izengshi.com");
			MailUtil.sendDelayMail(tos, "信用卡消费提醒", "尊敬的持卡人：</br>\n&nbsp;&nbsp;&nbsp;&nbsp;您于2014-06-15在广西北海巴伐利亚酒店消费325.00元，请迅速还款！");
			log.info("sssssss");
		} catch (PaasException e) {
			e.printStackTrace();
		}
	}
}
