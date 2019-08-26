package com.zengshi.ecp.frame.bean;

import com.zengshi.ecp.frame.context.EcpFrameContextHolder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**动态spring 容器bean定义
 */
public class BeanExtendManager {
    private final static Logger logger=Logger.getLogger(BeanExtendManager.class);

    /**
     * 销毁bean
     * @param beanName
     */
    public static void destroy(String beanName){
        ConfigurableApplicationContext  ctx= (ConfigurableApplicationContext) EcpFrameContextHolder.getContext();
        DefaultListableBeanFactory beanFactory= (DefaultListableBeanFactory) ctx.getBeanFactory();
        logger.info("销毁bean："+beanName);
        if(beanFactory.containsBean(beanName)){
            beanFactory.destroySingleton(beanName);
            beanFactory.destroyScopedBean(beanName);
            beanFactory.removeBeanDefinition(beanName);
        }else{
            logger.info(String.format("No %s defined in bean container.",beanName));
        }
    }

    /**
     * 销毁bean
     * @param beanClass
     */
    public static void destory(Class<?> beanClass){
        destroy(beanClass.getSimpleName());
    }

    /**
     * 注册bean
     * @param beanClass
     */
    public static void addBean(Class<?> beanClass){
        addBean(beanClass,beanClass.getSimpleName());
    }

    /**
     * 注册bean
     * @param beanClass
     * @param beanName
     */
    public static void addBean(Class<?> beanClass,String beanName){
        ConfigurableApplicationContext  ctx= (ConfigurableApplicationContext) EcpFrameContextHolder.getContext();
        DefaultListableBeanFactory beanFactory= (DefaultListableBeanFactory) ctx.getBeanFactory();
        if(beanFactory.containsBeanDefinition(beanName)){
            BeanDefinitionBuilder builder=BeanDefinitionBuilder.genericBeanDefinition(beanClass);
            BeanDefinition definition=builder.getBeanDefinition();
            beanFactory.registerBeanDefinition(beanName,definition);
            logger.info(String.format("加载 %s 到bean容器......", beanClass.getName()));
        }else {
            logger.info(String.format("bean容器已存在 %s .......",beanClass.getName()));
        }
    }

    /**
     * 注册bean
     * @param bean
     */
    public static void addBean(BeanDefine bean){
        ConfigurableApplicationContext  ctx= (ConfigurableApplicationContext) EcpFrameContextHolder.getContext();
        DefaultListableBeanFactory beanFactory= (DefaultListableBeanFactory) ctx.getBeanFactory();
        boolean flag = bean.beanNameIsNull()?!containsType(bean.getInterfaceClass(),beanFactory):!beanFactory.containsBeanDefinition(bean.getBeanName());
        if(flag){
            BeanDefinitionBuilder builder=BeanDefinitionBuilder.genericBeanDefinition(bean.getBeanClass());
            BeanDefinition definition=builder.getBeanDefinition();
            if(StringUtils.hasText(bean.getScope())){
                builder.setScope(bean.getScope().trim());
            }
            if(StringUtils.hasText(bean.getInitMethodName())){
                builder.setInitMethodName(bean.getInitMethodName());
            }
            if(StringUtils.hasText(bean.getDestroyMethodName())){
                builder.setDestroyMethodName(bean.getDestroyMethodName());
            }
            if(!CollectionUtils.isEmpty(bean.getAttrMap())){
                Set<Map.Entry<String,Object>> entries=bean.getAttrMap().entrySet();
                for(Map.Entry<String,Object> entry : entries){
                    builder.addPropertyValue(entry.getKey(),entry.getValue());
                }
            }
            if(!CollectionUtils.isEmpty(bean.getBeanRefMap())){
                Set<Map.Entry<String,String>> entries=bean.getBeanRefMap().entrySet();
                for(Map.Entry<String,String> entry : entries){
                    builder.addPropertyReference(entry.getKey(),entry.getValue());
                }
            }
            if(StringUtils.hasText(bean.getDependsOn())){
                builder.addDependsOn(bean.getDependsOn());
            }
            builder.setAutowireMode(bean.getAutowireMode());
            builder.setDependencyCheck(bean.getDependencyCheck());
            builder.setLazyInit(bean.isLazy());
            beanFactory.registerBeanDefinition(bean.getBeanName(),definition);
            logger.info(String.format("加载 %s 到bean容器......", bean.getBeanClass().getName()));
        }else {
            logger.info(String.format("bean容器已存在 %s 的对象，未加载 %s .......",bean.getInterfaceClass().getName(),bean.getBeanClass().getName()));
        }
    }

    public static boolean containsType(Class<?> type,DefaultListableBeanFactory beanFactory){
        Map objMap=beanFactory.getBeansOfType(type);
        //CollectionUtils.isEmpty(beanFactory.getBeansOfType(bean.getInterfaceClass()))
        return objMap!=null && objMap.size()>0;
    }

    public static void main(String[] args){
        System.out.println(String.format("No %s defined in bean container.","cdds"));

        Set<BeanDefine> bds=new HashSet<BeanDefine>();
        BeanDefine bd=new BeanDefine();
        bd.setBeanName("aaa");
        bd.setInterfaceClass(com.zengshi.ecp.frame.bean.BeanDefine.class);
        BeanDefine db1=new BeanDefine();
        db1.setInterfaceClass(com.zengshi.ecp.frame.bean.BeanDefine.class);
        bds.add(bd);
        bds.add(db1);

        System.out.println("===============:  "+bds.size());

        System.out.println("***********:  "+db1.beanNameIsNull() +"    "+bd.beanNameIsNull());
    }
}
