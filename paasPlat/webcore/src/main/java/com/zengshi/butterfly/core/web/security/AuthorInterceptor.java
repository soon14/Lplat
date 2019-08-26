
package com.zengshi.butterfly.core.web.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.zengshi.butterfly.core.service.URLBroken;
import com.zengshi.butterfly.core.web.cookie.CookiesConfig;
import com.zengshi.butterfly.core.web.security.AuthorizationResult.ResultEnum;


@Deprecated
public class AuthorInterceptor extends HandlerInterceptorAdapter {

    /** Logger available to subclasses */
    protected final Log logger = LogFactory.getLog(getClass());


    @Autowired
    private AuthorizationModule authorizationModule;
    
    @Autowired
    private CookiesConfig cookiesConfig;
    
    @Autowired
    private URLBroken urlBroken;

    
	public void setCookiesConfig(CookiesConfig cookiesConfig) {
		this.cookiesConfig = cookiesConfig;
	}
    
   
    public void setAuthorizationModule(AuthorizationModule authorizationModule) {
        this.authorizationModule = authorizationModule;
    }

    // before the actual handler will be executed
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 构建cookieModule
    	CookieUser user=UserCookieUtil.getUserCookie(request,cookiesConfig.getCookieModel(CookiesConfig.LOGIN_INFO));
    	
    	if(handler instanceof HandlerMethod) {
    		HandlerMethod method=(HandlerMethod)handler;
    		//method.getMethodAnnotation();
    	}
    	
    	if(authorizationModule.isFreePage(request, response)) {
    		return true;
    	}
    	//已经失效
    	if(user != null && user.isExpired()) {
    		//AuthorizationResult result=new AuthorizationResult();
    		//result.setResult(ResultEnum.EXPIRED);
    		response.sendRedirect(urlBroken.get(URLBroken.URL_EXPIRED));
    		return false;
    	}
    	
    	LoginInfoHelper.set(user);

        // 安全控制
        AuthorizationResult result = authorizationModule.isPermitted(request, response);
        if (result.isOk()) {
            return true;
        } else {
            response.sendRedirect(result.getRedirectPage());
            return false;
        }
    }
    
    /**
     * This implementation is empty.
     */
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
    	if(cookiesConfig != null) {
    		CookieUser user=UserCookieUtil.getUserCookie(request,cookiesConfig.getCookieModel(CookiesConfig.LOGIN_INFO));
            if(user != null) {
            	 UserCookieUtil.write(request, response, user,cookiesConfig.getCookieModel(CookiesConfig.LOGIN_INFO));
            } 
    	}
        
    }


	public void setUrlBroken(URLBroken urlBroken) {
		this.urlBroken = urlBroken;
	}

    
}
