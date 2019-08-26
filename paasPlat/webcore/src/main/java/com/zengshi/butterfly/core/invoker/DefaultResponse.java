/**
 * 
 */
package com.zengshi.butterfly.core.invoker;

/**
 *
 */
public class DefaultResponse implements Response {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4385703863011436763L;

	protected String code;
	
	protected String msg;
	
	
	public DefaultResponse(String code, String msg) {
		super();
		this.code = code;
		this.msg = msg;
	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return this.code;
	}

	@Override
	public String getMsg() {
		// TODO Auto-generated method stub
		return this.msg;
	}
	

}
