package com.zengshi.butterfly.core.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import com.zengshi.butterfly.core.web.helper.WebHelper;
import com.zengshi.butterfly.logger.LoggerCatalog;

public class StopWatchHandlerInterceptor extends AbstractCustomerHandlerInterceptor {
	
	private long consumTime=3000;
	
	private Logger accessWarnninglogger=LoggerFactory.getLogger(LoggerCatalog.ACCESS_TIME_WARNNING);
	
	private Logger accessLogger=LoggerFactory.getLogger(LoggerCatalog.ACCESS);
	
	private static ThreadLocal<StopWatch> stopWatchLocal=new ThreadLocal<StopWatch>();

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		StopWatch stopWatch = new StopWatch(handler.toString());  
        stopWatchLocal.set(stopWatch);  
        stopWatch.start(handler.toString());  
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		StopWatch stopWatch = stopWatchLocal.get();  
        stopWatch.stop();  
        stopWatchLocal.set(null);
        
        long consumeTime=stopWatch.getTotalTimeMillis();
		String msg=String.format("%s:%s consume %d millis ",request.getMethod(),
				request.getRequestURI(), consumeTime);
		
		accessLogger.info(msg);
		//为了统计链接信息，配合pvuv的改造，所以取消PVUV监控日志的输出时间控制
//		if (consumeTime > this.consumTime) {
			// 记录到日志文件
			boolean isAjaxFlag = this.isAjaxReq(request);
			accessWarnninglogger.info(msg+";request param "+WebHelper.printRequest(request)+";isAjax " + String.valueOf(isAjaxFlag));
//		}
	}

	public long getConsumTime() {
		return consumTime;
	}

	public void setConsumTime(long consumTime) {
		this.consumTime = consumTime;
	}
	
	private boolean isAjaxReq(HttpServletRequest request){
    	String requestedWith = request.getHeader("x-requested-with"); 
        // 表示是一个AJAX POST请求
        if ("XMLHttpRequest".equalsIgnoreCase(requestedWith)) {
        	return true;
        }
        return false;
    }
}
