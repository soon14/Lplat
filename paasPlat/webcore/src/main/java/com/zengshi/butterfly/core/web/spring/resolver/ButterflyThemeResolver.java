/**
 * 
 */
package com.zengshi.butterfly.core.web.spring.resolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.WebUtils;

import com.zengshi.butterfly.core.config.Application;
import com.zengshi.butterfly.core.web.WebConstants;

/**
 *
 */
public class ButterflyThemeResolver extends CookieGenerator implements ThemeResolver {

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.ThemeResolver#resolveThemeName(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public String resolveThemeName(HttpServletRequest request) {
		// TODO Auto-generated method stub
		String theme=request.getParameter(Application.getValue(WebConstants.THEME_REQUEST_PARAM_KEY));
		
		if(theme == null || "".equals(theme.trim())) {
			theme=request.getAttribute(WebConstants.THEME_REQUEST_ATTR_KEY)==null?null:request.getAttribute(WebConstants.THEME_REQUEST_ATTR_KEY).toString();
		}
		if(theme == null || "".equals(theme.trim())) {
			Cookie cookie=WebUtils.getCookie(request, WebConstants.THEME_COOKIE_KEY);
			theme=cookie == null?null:cookie.getValue();
		}
		if(theme == null || "".equals(theme.trim())) {
			theme=request.getSession().getAttribute(WebConstants.THEME_SESSION_KEY) == null?null:request.getSession().getAttribute(WebConstants.THEME_SESSION_KEY).toString();
		}
		
		return (theme==null || "".equals(theme.trim())) ?this.getDefaultThemeName():theme.trim();
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.ThemeResolver#setThemeName(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	@Override
	public void setThemeName(HttpServletRequest request,
			HttpServletResponse response, String themeName) {
		if (themeName != null) {
			// Set request attribute and add cookie.
			request.setAttribute(WebConstants.THEME_REQUEST_ATTR_KEY, themeName);
			addCookie(response, themeName);
		}

		else {
			// Set request attribute to fallback theme and remove cookie.
			request.setAttribute(WebConstants.THEME_REQUEST_ATTR_KEY, getDefaultThemeName());
			removeCookie(response);
		}

	}
	
	private String getDefaultThemeName() {
		return Application.getValue(WebConstants.THEME_DEFAULT);
	}

}
