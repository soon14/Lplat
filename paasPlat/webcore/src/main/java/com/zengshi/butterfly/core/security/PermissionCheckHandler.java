package com.zengshi.butterfly.core.security;

import com.zengshi.butterfly.core.annotation.Security;

public interface PermissionCheckHandler {

	public boolean isPermission(Security security) throws Exception;
}
