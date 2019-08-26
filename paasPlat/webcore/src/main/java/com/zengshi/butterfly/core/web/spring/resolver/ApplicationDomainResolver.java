package com.zengshi.butterfly.core.web.spring.resolver;

import javax.servlet.http.HttpServletRequest;

import com.zengshi.butterfly.core.config.Application;
import com.zengshi.butterfly.core.web.WebConstants;

public class ApplicationDomainResolver implements DomainResolver,WebConstants {

	@Override
	public String resolverDomainName(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return Application.getValue(THEME_DOMAIN_KEY);
	}

}
