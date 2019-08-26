
/** 
 * Date:2015-7-22下午3:35:20 
 * 
 */ 
package com.zengshi.paas.utils;

import java.util.Locale;

import com.zengshi.paas.resource.PaasReloadableResourceBundleMessageSource;

/**
 * Description: 用于资源文件的解析<br>
 * Date:2015-7-22下午3:35:20  <br>
 * 
 * @version  
 * @since JDK 1.6 
 */
public class ResourceMsgUtil {
    
    /**
     * 
     * getMessage:根据资源文件，获取提示信息 <br/>
     * 
     * @param code
     * @param keys
     * @param locale
     * @return 
     * @since JDK 1.6
     */
    public static String getMessage(String code, Object[] keys, Locale locale){
        
        PaasReloadableResourceBundleMessageSource msg = PaasContextHolder.getContext().getBean("messageSource", PaasReloadableResourceBundleMessageSource.class);
        if(locale == null){
            locale = Locale.getDefault();
        }
        return msg.getMessage(code, keys, locale);
    }
    
    /**
     * 
     * getMessage: 从资源文件中，生成信息描述 <br/> 
     * 其中使用的locale 为 默认的中文<br/> 
     * 
     * @param code
     * @param keys
     * @return 
     * @since JDK 1.6
     */
    public static String getMessage(String code, Object[] keys){
        
        return ResourceMsgUtil.getMessage(code, keys, LocaleUtil.getLocale());
    }

}


