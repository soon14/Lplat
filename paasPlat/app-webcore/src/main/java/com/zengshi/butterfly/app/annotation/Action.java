package com.zengshi.butterfly.app.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.TYPE)
public @interface Action {

	/**
	 * action 对应的业务编码
	 * @return
	 */
	String bizcode() default "";
	/**
	 * 是否需要验证用户
	 * @return
	 */
	boolean userCheck() default true;
	
	/** 接口是否需要ip验证 **/
	boolean ipCheck() default false;
	
	/** 用户调用接口权限验证 **/
	boolean rightCheck() default false;
	
	/** 是否加密，默认不加密 **/
	boolean encrypt() default false;
}
