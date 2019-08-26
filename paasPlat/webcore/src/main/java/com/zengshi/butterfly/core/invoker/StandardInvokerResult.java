/**
 * 
 */
package com.zengshi.butterfly.core.invoker;

/**
 *
 */
public class StandardInvokerResult<T extends InvokerObject> implements InvokerResult<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4987717650176240721L;

	private Response response;

	private T result;
	
	public StandardInvokerResult() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public StandardInvokerResult(T result) {
		super();
		this.result = result;
	}

	@Override
	public Response getResponse() {
		// TODO Auto-generated method stub
		return this.response;
	}

	@Override
	public T getResult() {
		// TODO Auto-generated method stub
		return this.result;
	}

	@Override
	public void setResponse(Response response) {
		// TODO Auto-generated method stub
		this.response=response;
	}

	@Override
	public T setResult(T result) {
		// TODO Auto-generated method stub
		return this.result=result;
	}

}
