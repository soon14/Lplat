package com.zengshi.butterfly.app.filter;

import com.zengshi.butterfly.app.ErrorCodeDefine;
import com.zengshi.butterfly.app.MappContext;
import com.zengshi.butterfly.app.model.IAppDatapackage;
import com.zengshi.butterfly.core.exception.BusinessException;
import com.zengshi.butterfly.core.exception.DefaultExceptionCode;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class IntegrityRequestFilter implements IFilter<Map<String,Object>> {

	Logger logger = LoggerFactory.getLogger(IntegrityRequestFilter.class);
	
	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String,Object> doFilter(Map<String,Object> context) throws Exception {
		
		if(context == null)
			throw new BusinessException(new DefaultExceptionCode(ErrorCodeDefine.EXPECT_ERROR,"APP 上下文信息为 NULL"));
		
		IAppDatapackage request = (IAppDatapackage)context.get(MappContext.MAPPCONTEXT_REQUEST);
		
		if(request==null)
			throw new BusinessException(new DefaultExceptionCode(ErrorCodeDefine.EXPECT_ERROR,"APP 请求信息为 NULL"));
		
		if(request.getHeader() == null || StringUtils.isBlank(request.getHeader().getBizCode()))
			throw new BusinessException(new DefaultExceptionCode(ErrorCodeDefine.EXPECT_ERROR,"APP BIZCODE 为空"));
		
		if(StringUtils.isBlank(request.getHeader().getIdentityId()))
			logger.warn("APP 报文流水为空");
		
		logger.info("请求信息："+context.get(MappContext.MAPPCONTEXT_REQUEST_STRING));
		
		return context;
		
	}

}
