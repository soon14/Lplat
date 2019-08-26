package com.zengshi.paas.utils;

import com.zengshi.paas.config.SystemPropertiesManager;

import java.util.Map;

/**zk节点内容获取工具类
 */
public class SystemPropertiesUtil {
    private static SystemPropertiesManager manager;
    static {
        manager=PaasContextHolder.getContext().getBean("systemPropertiesManager",SystemPropertiesManager.class);
    }

    /**
     * 获取zk节点内容属性值
     * @param key
     * @return
     */
    public static Object getPropertiesValue(String key){

        return manager.getPropertiesValue(key);
    }

    /**
     * 获取zk节点内容对象
     * @return map
     */
    public static Map<String,Object> getPropertiesObject(){

        return manager.getPropertiesObject();
    }
}
