package com.zengshi.butterfly.core.cache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zengshi.paas.session.SessionHttpServletRequestWrapper;
import com.zengshi.paas.session.SessionManager;
import com.zengshi.butterfly.core.spring.BeanFactory;
import com.zengshi.butterfly.core.web.HttpRequestFactory;

public class CacheHttpRequestFactory implements HttpRequestFactory {

	@Override
	public HttpServletRequest getRequest(HttpServletRequest request,
			HttpServletResponse response) {
		SessionManager sessionManager = BeanFactory.getObject("sessionManager");
	    SessionHttpServletRequestWrapper requestWrapper = new SessionHttpServletRequestWrapper(request, response, sessionManager);		
	    return requestWrapper;
	}

}
