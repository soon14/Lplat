package com.zengshi.butterfly.core.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;

import com.zengshi.butterfly.core.web.security.AuthorUserPrincipal;
import com.zengshi.butterfly.core.web.security.LoginInfoHelper;

public class LoggerMDCHandlerInterceptor extends AbstractCustomerHandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		// TODO Auto-generated method stub
		AuthorUserPrincipal user=LoginInfoHelper.get();
		if(user != null) {
			MDC.put("userName", user.getNickName());
			MDC.put("accessIp",request.getRemoteAddr());
		}
		
		
		return super.preHandle(request, response, handler);
	}

}
