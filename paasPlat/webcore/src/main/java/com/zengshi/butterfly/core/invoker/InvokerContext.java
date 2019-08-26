package com.zengshi.butterfly.core.invoker;

import java.io.Serializable;

public interface InvokerContext<T extends InvokerObject> extends Serializable{

	public T getInvokerObject();
	 
}
