package com.zengshi.butterfly.core.config;

public interface Injectable {
	
   public <T> boolean injectConfig(T t);
   
   public <T> T injectConfig(Class<T> clazz);
   
}
