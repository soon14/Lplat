package com.zengshi.butterfly.app.exception;

import com.zengshi.butterfly.core.exception.BusinessException;

public class MappException extends BusinessException {

	public  MappException(MappExceptionCode exCode, Throwable t) {
		super(exCode, t);
	}

	public  MappException(MappExceptionCode exCode) {
		super(exCode);
	}
}
