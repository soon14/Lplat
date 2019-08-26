/**
 * 
 */
package com.zengshi.butterfly.core.exception;

/**
 *
 */
public class BusinessException extends BaseException {

	public <T extends ExceptionCode> BusinessException(T exCode) {
		super(exCode);
		// TODO Auto-generated constructor stub
	}

	public <T extends ExceptionCode> BusinessException(T exCode, Throwable t) {
		super(exCode, t);
		// TODO Auto-generated constructor stub
	}

	
}
