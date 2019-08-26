/**
 * 
 */
package com.zengshi.butterfly.core;

/**
 *
 */
public abstract class  TransMessage<R> implements ITransMessage<R> {

	/**
	 * request
	 */
	protected R r;
	
	public TransMessage() {
		super();
	}

	public TransMessage(R r) {
		super();
		this.r = r;
	}

	@Override
	public void setRequest(R r) {
		this.r=r;
		
	}

	@Override
	public R getRequest() {
		// TODO Auto-generated method stub
		return this.r;
	}

}
