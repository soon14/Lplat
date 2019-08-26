package com.zengshi.butterfly.core.invoker;



public interface ButterflyService<Req extends InvokerObject,Result extends InvokerObject> {

	public InvokerResult<Result>  execute(InvokerContext<Req> context) throws Exception;
}
