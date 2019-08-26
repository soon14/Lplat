package com.zengshi.butterfly.app.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * 请求报文日志
 *
 */
@Component
@Aspect
public class LogAspect {
	
	Logger logger = LoggerFactory.getLogger("mapp.reqAndrsp.message");

	@Before("execution(* com.zengshi.butterfly.app.client.StringClientHandler.doHandle(..)) && args(request,attributes)")
	public void requestLog(String request, Map<String, Object> attributes) throws Throwable
	{
		if (logger.isInfoEnabled()) {
			logger.info("request log: request message~O(∩_∩)O~ {}, the context is ~O(∩_∩)O~ {}", request, attributes);
		}
	}
	
	@AfterReturning(value="execution(* com.zengshi.butterfly.app.client.StringClientHandler.doHandle(..)) ",returning="response")
	public void responseLog(String response) throws Throwable
	{
		if (logger.isInfoEnabled()) {
			logger.info("response log: {}", response);
		}
	}
	
	
}
