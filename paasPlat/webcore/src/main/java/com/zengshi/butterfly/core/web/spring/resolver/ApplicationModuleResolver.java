/**
 * 
 */
package com.zengshi.butterfly.core.web.spring.resolver;

import javax.servlet.http.HttpServletRequest;

import com.zengshi.butterfly.core.config.Application;
import com.zengshi.butterfly.core.web.WebConstants;

/**
 *
 */
public class ApplicationModuleResolver implements ModuleResolver,WebConstants {

	/* (non-Javadoc)
	 * @see com.zengshi.butterfly.core.web.spring.resolver.ModuleResolver#resolverDomainName(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public String resolverDomainName(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return Application.getValue(THEME_MODULE_KEY);
	}

}
