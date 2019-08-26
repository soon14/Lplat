package com.zengshi.butterfly.core.web.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthorizationModule {

	boolean isFreePage(HttpServletRequest request, HttpServletResponse response);
    /**
     * 验证处理
     * 
     * @param request
     * @param response
     * @return
     */
    AuthorizationResult isPermitted(HttpServletRequest request, HttpServletResponse response);

}
