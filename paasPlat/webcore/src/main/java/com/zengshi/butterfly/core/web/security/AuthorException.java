/**
 * 
 */
package com.zengshi.butterfly.core.web.security;

import com.zengshi.butterfly.core.exception.BaseException;
import com.zengshi.butterfly.core.exception.DefaultExceptionCode;

/**
 *
 */
public class AuthorException  extends BaseException {

	private AuthorizationResult result;
	

	public AuthorException(AuthorizationResult result) {
		super(new DefaultExceptionCode(result.getResult().toString(),result.getMessage()));
		this.result = result;
	}

	public AuthorizationResult getResult() {
		return result;
	}

	public void setResult(AuthorizationResult result) {
		this.result = result;
	}
	
}
