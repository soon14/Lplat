package com.zengshi.butterfly.core.web.spring.resolver;

import javax.servlet.http.HttpServletRequest;

public interface DomainResolver {

	public String resolverDomainName(HttpServletRequest request);
}
