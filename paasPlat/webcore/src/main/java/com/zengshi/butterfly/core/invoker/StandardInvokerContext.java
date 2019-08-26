package com.zengshi.butterfly.core.invoker;

public class StandardInvokerContext<T extends InvokerObject> implements InvokerContext<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private T invokerObject;

	public T getInvokerObject() {
		return invokerObject;
	}
	public StandardInvokerContext( T invokerObject) {
		super();
		this.invokerObject = invokerObject;
	}
	
	
	
}
