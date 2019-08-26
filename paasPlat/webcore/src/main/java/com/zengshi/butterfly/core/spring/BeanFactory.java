/**
 * 
 */
package com.zengshi.butterfly.core.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 
 */
public class BeanFactory implements ApplicationContextAware {

	private static ApplicationContext context;

	private BeanFactory() {

	}

	public static void setContext(ApplicationContext applicationContext) {
		if (context == null) {
			synchronized (BeanFactory.class) {
				if (context == null) {
					context = applicationContext;
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	public static <T> T getObject(String name) {
		return (T) context.getBean(name);
	}

	/**
	 * <p>描述: 原来以名字注入的，现改为类型</p> 
	 * @param <T>
	 * @param name
	 * @param type
	 * @return  
	 * @Date          2014-1-23 下午04:58:44
	 */
	public static <T> T getObject(String name, Class<T> type) {
//		return context.getBean(name, type);
		return context.getBean(type);
	}
	
	public static <T> T getObject(Class<T> type) {
		return context.getBean(type);
	}

	public static org.springframework.beans.factory.BeanFactory register(
			String name, Class<?> clazz) {
		return register(name,clazz,AutowireCapableBeanFactory.AUTOWIRE_BY_NAME,true);
	}

	/**
	 * 向spring注册一个自定义的类
	 * @param name
	 * @param clazz
	 * @param autowireMode
	 * @param dependencyCheck
	 * @return
	 */
	public static org.springframework.beans.factory.BeanFactory register(
			String name, Class<?> clazz,int autowireMode, boolean dependencyCheck ) {
		
		 DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)context.getAutowireCapableBeanFactory();

		AbstractBeanDefinition newsProvider = new RootBeanDefinition(clazz,
				autowireMode, dependencyCheck);
		beanFactory.registerBeanDefinition(name, newsProvider);
		context.getAutowireCapableBeanFactory().createBean(clazz, autowireMode, dependencyCheck);
		
		return (org.springframework.beans.factory.BeanFactory) beanFactory;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		if (context == null) {
			synchronized (BeanFactory.class) {
				if (context == null) {
					context = applicationContext;
				}
			}
		}
	}
}
