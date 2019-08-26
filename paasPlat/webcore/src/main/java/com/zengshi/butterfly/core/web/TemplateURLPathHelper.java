package com.zengshi.butterfly.core.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.UrlPathHelper;

public class TemplateURLPathHelper extends UrlPathHelper implements ButterflyWebConstants {
	
	@Override
	public String getLookupPathForRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		String controllerTempalte=request.getAttribute(KEY_CONTROLLER_TEMPLATE_URL)==null?"":request.getAttribute(KEY_CONTROLLER_TEMPLATE_URL).toString();
		if(controllerTempalte != null && !"".equals(controllerTempalte)) {
			return controllerTempalte;
			//return getRemainingPath(controllerTempalte, request.getContextPath(), true);
		}
		return super.getLookupPathForRequest(request);
	}

	private String getRemainingPath(String requestUri, String mapping, boolean ignoreCase) {
		int index1 = 0;
		int index2 = 0;
		for ( ; (index1 < requestUri.length()) && (index2 < mapping.length()); index1++, index2++) {
			char c1 = requestUri.charAt(index1);
			char c2 = mapping.charAt(index2);
			if (c1 == ';') {
				index1 = requestUri.indexOf('/', index1);
				if (index1 == -1) {
					return null;
				}
				c1 = requestUri.charAt(index1);
			}
			if (c1 == c2) {
				continue;
			}
			if (ignoreCase && (Character.toLowerCase(c1) == Character.toLowerCase(c2))) {
				continue;
			}
			return null;
		}
		if (index2 != mapping.length()) {
			return null;
		}
		if (index1 == requestUri.length()) {
			return "";
		}
		else if (requestUri.charAt(index1) == ';') {
			index1 = requestUri.indexOf('/', index1);
		}
		return (index1 != -1) ? requestUri.substring(index1) : "";
	}
}
