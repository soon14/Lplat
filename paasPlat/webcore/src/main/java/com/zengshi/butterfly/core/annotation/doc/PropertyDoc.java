package com.zengshi.butterfly.core.annotation.doc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 属性描述性文档，不用来做验证
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PropertyDoc {

	String comment() default "";
	
	/** 属性名称**/
	String name () default "";
	
	/** 属性类型入 String(12)**/
	Type type() ;
	
	/** 长度 **/
	int length() default 0;
	
	/** 是否可以为空**/
	boolean empty() default true;
	
	/** 默认值 **/
	String defVal() default "无";
	
	 
	
	
}
