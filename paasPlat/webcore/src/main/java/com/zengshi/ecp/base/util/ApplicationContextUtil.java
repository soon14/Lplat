/** 
 * Date:2015年11月14日上午10:42:44 
 * 
*/
package com.zengshi.ecp.base.util;

import com.zengshi.paas.utils.PaasContextHolder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** 
 * Description: <br>
 * Date:2015年11月14日上午10:42:44  <br>
 * 
 * @since JDK 1.6 
 * @see       
 */
public class ApplicationContextUtil implements ApplicationContextAware{

    private static ApplicationContext ctx= null;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtil.ctx=applicationContext;
    }
    /**
     * 获取
     * @param ctx
     */
    public static void setContext(ApplicationContext ctx){
        ApplicationContextUtil.ctx = ctx;
    }
    
    /**
     * 获取上下文
     */
    public static ApplicationContext getContext(){
        return ctx;
    }
    
    /**
     * 根据 bean 的Id，以及类型，获取Context中的信息；如果在ecp的框架中没有，会重新查找PaasContext 中的Bean
     * @param name
     * @param clazz
     * @return
     */
    public static <T> T getBean(String name, Class<T> clazz){
        
        T obj = ApplicationContextUtil.getContext().getBean(name, clazz);
        
        if(obj == null){
            return PaasContextHolder.getContext().getBean(name, clazz);
        } else {
            return obj;
        }
        
    }
    
    public static <T> List<T> getBeans(Class<T> clazz){
        Map<String,T> map=ApplicationContextUtil.getContext().getBeansOfType(clazz);
        List<T> list=new ArrayList<T>();
        if(null!=map){
            for(T t : map.values()){
                list.add(t);
            }
        }
        return list;
    }

}

