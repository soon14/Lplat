package com.zengshi.butterfly.core.monitor;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zengshi.butterfly.core.annotation.Config;
import com.zengshi.butterfly.core.annotation.Monitor;
import com.zengshi.butterfly.core.web.helper.WebHelper;
import com.zengshi.butterfly.logger.LoggerCatalog;

@Aspect
public class MonitorAspect {
	private Logger monitorLog = LoggerFactory
			.getLogger(LoggerCatalog.MONITOR_TIMES);
	private static final String SEPARATOR = "()";
	
	@Config("monitor.permission")
	private boolean monitorPermission;
	
	@Config("monitor.servicetimingwarn")
	private long ServiceTimingWarnThresholdMsec; 
	
	@Around(value="@annotation(monitor)")
	public Object monitorTimes(ProceedingJoinPoint point, Monitor monitor) throws Throwable {
		MethodSignature joinPointObject = (MethodSignature) point.getSignature();  
		
		Object[] args = point.getArgs();
		
		boolean isDebug = WebHelper.isDebugMode();
		
		if(!isDebug) {
			if (!monitorPermission) 
			{
				return point.proceed(args);
			}
		}
		
		Object object = null;
		if (monitorPermission || isDebug) {
			long begin = System.currentTimeMillis();
			object = point.proceed(args);
			long use = System.currentTimeMillis() - begin;
			if ((use > ServiceTimingWarnThresholdMsec) || isDebug) 
			{
				Method method = joinPointObject.getMethod();    
				StringBuilder sb = new StringBuilder();
				sb.append(point.getTarget().getClass().getName()).append(SEPARATOR);
				sb.append(method.getName()).append(SEPARATOR);
				sb.append("Parameters:");
				int length = args.length;
				for (int i = 0; i < length; i++) {
					sb.append(args[i]).append(SEPARATOR);
				}
				sb.append("use time:").append(use).append("ms");
				monitorLog.info(sb.toString());
			}
		}
		return object;

	}
}
