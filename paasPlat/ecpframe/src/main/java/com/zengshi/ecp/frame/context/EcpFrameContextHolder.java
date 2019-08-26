/**
 * 
 */
package com.zengshi.ecp.frame.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.zengshi.paas.utils.PaasContextHolder;

/**
 *
 */
public class EcpFrameContextHolder implements ApplicationContextAware{
	
	private static ApplicationContext ctx= null;
	
	@Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
	    EcpFrameContextHolder.ctx=applicationContext;
	}
	/**
	 * 获取
	 * @param ctx
	 */
	public static void setContext(ApplicationContext ctx){
		EcpFrameContextHolder.ctx = ctx;
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
		
		T obj = EcpFrameContextHolder.getContext().getBean(name, clazz);
		
		if(obj == null){
			return PaasContextHolder.getContext().getBean(name, clazz);
		} else {
			return obj;
		}
		
	}

	public static Object getBean(String name){
		Object obj=EcpFrameContextHolder.getContext().getBean(name);
		if(null==obj){
			return PaasContextHolder.getContext().getBean(name);
		}else{
			return obj;
		}
	}
	
	public static <T> List<T> getBeans(Class<T> clazz){
	    Map<String,T> map=EcpFrameContextHolder.getContext().getBeansOfType(clazz);
	    List<T> list=new ArrayList<T>();
	    if(null!=map){
	        for(T t : map.values()){
	            list.add(t);
	        }
	    }
	    return list;
	}

    

}
