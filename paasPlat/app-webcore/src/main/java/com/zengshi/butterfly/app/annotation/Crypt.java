package com.zengshi.butterfly.app.annotation;

import com.zengshi.butterfly.core.security.encrypt.EncrpytEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface Crypt {

	/**
	 * 加密算法
	 * @return
	 */
	EncrpytEnum algorithm() default EncrpytEnum.DES;
	/**
	 * 解密秘钥
	 * @return
	 */
	String desKey() default "";
	
	/** 是否base64包裹 **/
	boolean base64Wrapped() default true;
	
	boolean gzip() default false;
	
	/** 是否签名 **/
	boolean sign() default false;
}
