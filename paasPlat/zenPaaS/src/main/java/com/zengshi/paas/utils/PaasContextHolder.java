package com.zengshi.paas.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 * zPaaS对象管理类
 *
 */
public class PaasContextHolder {
	private static ClassPathXmlApplicationContext ctx=null;
	static {
		ctx = new ClassPathXmlApplicationContext(new String[] { "paasContext.xml" });
	}
	
	public static ApplicationContext getContext() {
		return ctx;
	}

	public static void init(){

	}
}
