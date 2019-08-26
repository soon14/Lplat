package com.zengshi.ecp.frame.bean;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 */
public class BeanDefinitionManager implements ApplicationContextAware {
    private final Logger logger=Logger.getLogger(BeanDefinitionManager.class);
    private ApplicationContext ctx= null;
    private Set<BeanDefine> beanDefines=null;
    public BeanDefinitionManager() {
        beanDefines=new HashSet<BeanDefine>();
    }

    public void init(){
        Map<String,BeanSet> beanSetMap=ctx.getBeansOfType(BeanSet.class);
        logger.info("开始动态加载bean....................");

        if(!CollectionUtils.isEmpty(beanSetMap)){
            Set<Map.Entry<String,BeanSet>> entries= beanSetMap.entrySet();
            for(Map.Entry<String,BeanSet> entry : entries){
                BeanSet beanSet=entry.getValue();
                if(!CollectionUtils.isEmpty(beanSet)){
                    beanDefines.addAll(beanSet);
                }
            }
        }else{
            logger.info("没有发现需要动态加载的bean.................");
        }
        for(BeanDefine beanDefine : beanDefines){
            BeanExtendManager.addBean(beanDefine);
//            logger.info(String.format("加载类 %s成功......",beanDefine.getBeanClass()));
        }
        logger.info("动态bean加载结束...................");
    }

    public void destory(){
        logger.info("开始销毁动态加载的bean....................");
        for(BeanDefine beanDefine : beanDefines){

            BeanExtendManager.destroy(beanDefine.getBeanName());
            logger.info(String.format("销毁 %s 完成.....",beanDefine.getBeanClass()));
        }
        logger.info("动态bean销毁结束...................");
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx=applicationContext;
    }
}
