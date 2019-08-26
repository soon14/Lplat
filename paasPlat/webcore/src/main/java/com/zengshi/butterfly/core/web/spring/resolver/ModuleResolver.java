package com.zengshi.butterfly.core.web.spring.resolver;

import javax.servlet.http.HttpServletRequest;

public interface ModuleResolver {

	public String resolverDomainName(HttpServletRequest request);
}
