package com.zengshi.paas.message;

import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zengshi.paas.ConfigurationCenter;
import com.zengshi.paas.ConfigurationWatcher;
import com.zengshi.paas.PaasException;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import net.sf.json.JSONObject;

/**
 * 消息服务-消息发送类
 *
 */
public class MessageSender implements ConfigurationWatcher {
	public static final Logger log = Logger.getLogger(MessageSender.class);

	private String confPath = "/com/zengshi/paas/message/messageSender";

	private ConfigurationCenter cc = null;

	private Producer<String, Message> producer = null;

	private Properties props = null;
	
	private Random r = new Random();

	public void init() {
		if (log.isInfoEnabled()) {
			log.info("init MessageSender...");
		}
		try {
			process(cc.getConfAndWatch(confPath, this));
		} catch (PaasException e) {
			e.printStackTrace();
		}
	}

	public void process(String conf) {
		if (log.isInfoEnabled()) {
			log.info("new MessageSender configuration is received: " + conf);
		}
		JSONObject json = JSONObject.fromObject(conf);
		@SuppressWarnings("rawtypes")
		Iterator keys = json.keys();
		boolean changed = false;
		if (keys != null) {
			String key = null;
			while (keys.hasNext()) {
				key = (String) keys.next();

				if (props == null) {
					props = new Properties();
					changed = true;
				}
				if (props.containsKey(key)) {
					if (props.get(key) == null
							|| !props.get(key).equals(json.getString(key))) {
						props.put(key, json.getString(key));
						changed = true;
					}
				} else {
					props.put(key, json.getString(key));
					changed = true;
				}
			}
		}
		if (changed) {
			ProducerConfig cfg = new ProducerConfig(props);
			producer = new Producer<String, Message>(cfg);
		}
	}
	
	private int getRandomId() {
		return Math.abs(r.nextInt());
	}
	
	public void sendMessage(Object message, String topic) {
		Message msg = new Message();
		msg.setId(getRandomId());
		msg.setTopic(topic);
		msg.setMsg(message);
		KeyedMessage<String, Message> km = new KeyedMessage<String, Message>(topic,String.valueOf(msg.getId()), msg);
		producer.send(km);
	}

	public String getConfPath() {
		return confPath;
	}

	public void setConfPath(String confPath) {
		this.confPath = confPath;
	}

	public ConfigurationCenter getCc() {
		return cc;
	}

	public void setCc(ConfigurationCenter cc) {
		this.cc = cc;
	}

	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "clientSenderContext.xml" });
		MessageSender sender = (MessageSender)ctx.getBean("messageSender");
		sender.sendMessage("test", "message-topic");
	}
}
