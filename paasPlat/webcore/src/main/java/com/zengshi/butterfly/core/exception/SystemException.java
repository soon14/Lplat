package com.zengshi.butterfly.core.exception;

public class SystemException extends BaseException {

	public <T extends ExceptionCode> SystemException(T exCode) {
		super(exCode);
		// TODO Auto-generated constructor stub
	}

	public <T extends ExceptionCode> SystemException(T exCode, Throwable t) {
		super(exCode, t);
		// TODO Auto-generated constructor stub
	}
	
	
}
