/**
 * 
 */
package com.zengshi.butterfly.core.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CfgInject {
	
	String key() default "";
	   
	String property() default "";
	   
	String description() default "";
}
