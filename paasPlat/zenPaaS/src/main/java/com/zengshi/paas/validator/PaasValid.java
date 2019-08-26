/** 
 * Date:2015年8月4日下午5:01:10 
 * 
*/
package com.zengshi.paas.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
 * Description: bean数据验证 方法标识<br>
 * Date:2015年8月4日下午5:01:10  <br>
 * 
 * @since JDK 1.6 
 * @see       
 */
@Target({ElementType.METHOD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PaasValid {

}

