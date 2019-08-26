package com.zengshi.butterfly.app.exception;

import com.zengshi.butterfly.core.exception.ExceptionCode;

public class MappExceptionCode extends ExceptionCode {

	public static MappExceptionCode SUCCESS	= new MappExceptionCode("0000");
	public static MappExceptionCode UNRECOGNIZED_RESPONSE = new MappExceptionCode("0001");
	public static MappExceptionCode UNRECOGNIZED_REQUEST = new MappExceptionCode("0002");
	public static MappExceptionCode CONTEXT_ERROR = new MappExceptionCode("0003");
	public static MappExceptionCode UNEXPECT_ERROR = new MappExceptionCode("errorcode.unexpect_error");
	public static MappExceptionCode EXPECT_ERROR = new MappExceptionCode("errorcode.expect_error");
	
	protected MappExceptionCode(String key) {
		super("exception.mapp."+key);
	}

}
