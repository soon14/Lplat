package com.zengshi.butterfly.core.invoker;

import java.io.Serializable;

public interface InvokerResult<T extends InvokerObject> extends Serializable{
	
	public Response getResponse();
	
	public void setResponse(Response response);

	public T getResult();
	
	public T setResult(T result);
}
