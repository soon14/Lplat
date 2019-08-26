/**
 * 
 */
package com.zengshi.butterfly.core.invoker;

/**
 *
 */
public class ExceptionResponse  extends DefaultResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4326530266012481173L;
	
	private Throwable e;

	public ExceptionResponse(String code, String msg) {
		super(code, msg);
		// TODO Auto-generated constructor stub
	}

	public ExceptionResponse(String code, String msg,Throwable e) {
		super(code, msg);
		this.e=e;
		// TODO Auto-generated constructor stub
	}
	
	public Throwable getThrow() {
		return this.e;
	}
}
