package com.zengshi.butterfly.core.web.security;

import com.zengshi.butterfly.core.web.security.AuthorizationResult.ResultEnum;

public interface AuthorizationConfig {

	public String getAuthorizationPage(ResultEnum result);
}
