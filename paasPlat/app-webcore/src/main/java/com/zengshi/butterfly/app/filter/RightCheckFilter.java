package com.zengshi.butterfly.app.filter;

import com.zengshi.butterfly.app.ActionConfig;
import com.zengshi.butterfly.app.ApplicationConfig;
import com.zengshi.butterfly.app.ErrorCodeDefine;
import com.zengshi.butterfly.app.MappContext;
import com.zengshi.butterfly.app.model.IAppDatapackage;
import com.zengshi.butterfly.core.exception.BusinessException;
import com.zengshi.butterfly.core.exception.DefaultExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class RightCheckFilter implements IFilter<Map<String,Object>> {

	Logger logger = LoggerFactory.getLogger(RightCheckFilter.class);
	
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
		
		IAppDatapackage request = (IAppDatapackage)context.get(MappContext.MAPPCONTEXT_REQUEST);
		
		ActionConfig config = ApplicationConfig.getActionConfig(request.getHeader().getBizCode());
		
		if(config == null)
			return context;
		
		if(config.isRightCheck() == false)
			return context;
		
		Set<String> rights = (Set<String>) context.get(MappContext.MAPPCONTEXT_RIGHT);
		
		if(rights == null || rights.contains(MappContext.getRequest().getHeader().getBizCode()) == false)
		{
			throw new BusinessException(new DefaultExceptionCode(ErrorCodeDefine.EXPECT_ERROR,"用户未被允许请求该接口("+MappContext.getRequest().getHeader().getBizCode()+")"));
		}

		logger.debug("权限："+rights);
		
		return context;
	}

}
