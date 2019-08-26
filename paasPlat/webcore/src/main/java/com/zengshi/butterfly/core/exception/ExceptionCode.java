/**
 * 
 */
package com.zengshi.butterfly.core.exception;

import com.zengshi.butterfly.core.i18n.ButterMessage;

/**
 *
 */
public  class ExceptionCode extends ButterMessage {

	protected ExceptionCode(String key) {
		super(key);
		// TODO Auto-generated constructor stub
	}
	
	protected ExceptionCode(String key,String value) {
		super(key);
		// TODO Auto-generated constructor stub
	}

	public static ExceptionCode EX_CORE_UNKNOW=new ExceptionCode("exception.core.unknown");


	
}
