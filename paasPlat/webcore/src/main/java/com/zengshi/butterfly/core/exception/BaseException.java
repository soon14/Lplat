/**
 * 
 */
package com.zengshi.butterfly.core.exception;

/**
 *
 */
public abstract class BaseException extends Exception {

	private String errorCode;

	private String errorMsg;
	
	
	public <T extends ExceptionCode> BaseException(T exCode) {
		this.errorCode=exCode.getKey();
		this.errorMsg=exCode.getValue();
	}
	public <T extends ExceptionCode> BaseException(T exCode,Throwable t) {
		this.errorCode=exCode.getKey();
		this.errorMsg=exCode.getValue();
		this.initCause(t);
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	
}
