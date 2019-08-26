package com.zengshi.butterfly.core.exception;

public class DefaultExceptionCode extends ExceptionCode {

	private String message;
	
	public DefaultExceptionCode(String key) {
		super(key);
		// TODO Auto-generated constructor stub
	}
	
	public DefaultExceptionCode(String key,String msg) {
		super(key);
		this.message=msg;
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		if(this.message != null) return this.message;
		return super.getValue();
	}
	
	

}
