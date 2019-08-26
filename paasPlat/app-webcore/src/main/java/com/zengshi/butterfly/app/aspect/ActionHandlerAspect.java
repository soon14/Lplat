package com.zengshi.butterfly.app.aspect;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.zengshi.butterfly.app.ErrorCodeDefine;
import com.zengshi.butterfly.app.MappContext;
import com.zengshi.butterfly.app.filter.FilterChain;
import com.zengshi.butterfly.app.filter.FilterChainAware;
import com.zengshi.butterfly.core.exception.BusinessException;
import com.zengshi.butterfly.core.exception.DefaultExceptionCode;
import com.zengshi.butterfly.core.exception.SystemException;

/**
 * 实现带filter的切面定义Aspect模块
 *
 */
@Aspect
@Component
public class ActionHandlerAspect extends FilterChainAware<Map<String,Object>> {
	
	Logger logger = LoggerFactory.getLogger(ActionHandlerAspect.class);
	
	@Override
	@Autowired
	public void setFilterChain(@Qualifier("handlerFilterChain") FilterChain<Map<String,Object>> filterChain) {
		this.filterChain = filterChain;
	}
	
	@Override
	@Autowired(required=false)
	public void setCustomFilterChain(@Qualifier("customHandlerFilterChain") FilterChain<Map<String, Object>> customFilterChain) {
		this.customFilterChain = customFilterChain;
	}
	
	/**
	 * 拦截处理actionHandle方法，在此方法前执行已经定义的filter
	 * @param context
	 * @throws Throwable 
	 */
	@Around("execution(* com.zengshi.butterfly.app.base.BaseActionHandler.doHandle(..))")
	public void AroundActionHandler(ProceedingJoinPoint point) throws Throwable 
	{
		try{
			
			logger.debug("进入拦截器 ActionHandlerAspect.beforeActionHandler。");
			
			if(filterChain == null)
			{
				logger.debug("过滤器链为空，结束 ActionHandlerAspect.AroundServerHandler 拦截。");
				return;
			}
			
			/**
			 * 过滤器
			 */
			Map<String, Object> context = MappContext.getContext();
			
			if(filterChain != null)
				context = filterChain.doFilterChain(MappContext.getContext());
			if(customFilterChain != null)
				context = customFilterChain.doFilterChain(context);
			
			MappContext.initContext(context);
			
			logger.debug("结束 ActionHandlerAspect.AroundServerHandler 拦截。");
			
			point.proceed();
			
		}  catch (BusinessException e) {
			e.printStackTrace();
			logger.error(e.getErrorCode()+":"+e.getErrorMsg());
			MappContext.getResponse().getHeader().setRespCode(e.getErrorCode());
			MappContext.getResponse().getHeader().setRespMsg(e.getErrorMsg());
	
		} catch (SystemException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			MappContext.getResponse().getHeader().setRespCode(e.getErrorCode());
			MappContext.getResponse().getHeader().setRespMsg(e.getErrorMsg());
	
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			MappContext.getResponse().getHeader().setRespCode(ErrorCodeDefine.UNKNOW_ERROR);
			MappContext.getResponse().getHeader().setRespMsg(e.getMessage());
	
		}
		finally
		{
			filterChain.destroyFilterChain();
		}
	}
	
	@AfterReturning(value="execution(* com.zengshi.butterfly.app.base.BaseActionHandler.doHandle(..))")
	public void afterActionHandler() throws Exception
	{
		logger.debug("返回报文检测开始");
		
		if(MappContext.getContext() == null)
			throw new BusinessException(new DefaultExceptionCode("9999","APP上下文信息为 NULL"));
		
		if(MappContext.getResponse()==null)
			throw new BusinessException(new DefaultExceptionCode("9999","APP上下文中响应信息(Response)为 NULL"));
		
		if(MappContext.getResponse().getHeader() == null 
				|| StringUtils.isBlank(MappContext.getRequest().getHeader().getBizCode()))
			throw new BusinessException(new DefaultExceptionCode("9999","APP BizCode 为空"));
		
		if(StringUtils.isBlank(MappContext.getResponse().getHeader().getIdentityId()))
			logger.warn("APP 报文流水(IdentityId)为空");
		
		if(StringUtils.isBlank(MappContext.getResponse().getHeader().getRespCode()))
			logger.warn("APP 响应编码(RespCode)为空");
		
		logger.debug("返回报文检测结束");
		
	}

}
