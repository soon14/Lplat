/** 
 * Date:2015年7月28日下午2:21:25 
 * 
*/
package com.zengshi.ecp.frame.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;



/** 
 * Description: <br>
 * Date:2015年7月28日下午2:21:25  <br>
 * 
 * @since JDK 1.6 
 * @see       
 */
public class DBRuleMap<K, V> extends HashMap<K, V> {

    /** 
     * serialVersionUID: 
     * @since JDK 1.6 
     */ 
    private static final long serialVersionUID = 1678723871067643793L;
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public DBRuleMap(String configFile){
        super();
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(configFile);
        Map<String,RuleMap> map=ctx.getBeansOfType(RuleMap.class);
        if(null!=map && null!=map.values()){
            for(Map value : map.values()){
                this.putAll(value);
            }
        }
        ctx.close();
    }
}

