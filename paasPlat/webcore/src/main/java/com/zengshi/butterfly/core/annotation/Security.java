package com.zengshi.butterfly.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.zengshi.butterfly.core.security.PermissionCheckHandler;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Security {

	/**
	 * 是否必须登录,默认必须登录
	 * order 1
	 */
	 boolean mustLogin() default false;
	/**
	 * 用户权限处理类
	 * 可以是字符串或者类型
	 * order 4
	 */
	Class<? extends PermissionCheckHandler> authorCheckType() default PermissionCheckHandler.class;
	
	/**
	 * 限制登录用户名
	 * order 2
	 */
	String[] userName() default {};
	
	/**
	 * 限制登录用户角色
	 * order 3
	 */
	String[] role () default {};
	
	/**
	 * 请求地址信息
	 * 请求说明：用于说明请求的URL的用途
	 */
	String comment () default "";
	
	
}
