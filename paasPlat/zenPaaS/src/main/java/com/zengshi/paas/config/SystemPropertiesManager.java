package com.zengshi.paas.config;

import java.util.Map;
import java.util.Set;

/**{"db.application.conntext":"classpath*:/db/application-context-db-multi.xml"}
 */
public class SystemPropertiesManager extends ConfigManager{

    private Map<String,Object> map;

    @Override
    public void doHandle(Map<String, Object> map) {

        if(null==map){
            return;
        }
        Set<Map.Entry<String,Object>> entries=map.entrySet();
        for(Map.Entry<String,Object> entry : entries){
            String value=null==entry.getValue()?null:entry.getValue().toString();
            System.getProperties().setProperty(entry.getKey(),value);
        }

        this.map=map;
    }

    public Object getPropertiesValue(String key){
        if(null!=this.map){
            return this.map.get(key);
        }

        return null;
    }

    public Map<String,Object> getPropertiesObject(){

        return this.map;
    }
}
