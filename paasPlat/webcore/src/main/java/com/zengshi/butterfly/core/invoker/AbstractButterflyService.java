package com.zengshi.butterfly.core.invoker;

import javax.resource.NotSupportedException;

public abstract class AbstractButterflyService<Req extends InvokerObject,
											Result extends InvokerObject> implements ButterflyService<Req,Result> {

	@Override
	public InvokerResult<Result> execute(InvokerContext<Req> context) throws Exception {
		// TODO Auto-generated method stub
		throw new NotSupportedException("方法未支持");
	}
	
}
