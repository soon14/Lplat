package com.zengshi.butterfly.app.interfaces;

import com.zengshi.butterfly.app.model.IAppDatapackage;

import java.util.Map;


public interface IClientHandler<T extends IAppDatapackage> extends IHandler
{
	public T doHandlePackage(T requestObject,Map<String, Object> attributes) throws Exception;
}
