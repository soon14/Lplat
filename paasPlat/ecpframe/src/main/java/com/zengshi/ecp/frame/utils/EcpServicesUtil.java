/** 
 * Date:2015-8-6下午3:41:31 
 * 
 */ 
package com.zengshi.ecp.frame.utils;

import com.zengshi.ecp.frame.context.EcpFrameContextHolder;

/**
 * Description: 用于处理与 Context 相关的Bean的处理<br>
 * Date:2015-8-6下午3:41:31  <br>
 * 
 * @version  
 * @since JDK 1.6 
 */
public class EcpServicesUtil {
    
    /**
     * 
     * getBean: 根据Bean.id 以及Bean的接口类型，从 Spring Context中获取Bean；
     *  
     * @param name
     * @param clazz
     * @return 
     * @since JDK 1.6
     */
    public static <T> T getBean(String name, Class<T> clazz){
        return EcpFrameContextHolder.getBean(name, clazz);
    }

    public static Object getBean(String name){
        return EcpFrameContextHolder.getBean(name);
    }

}

