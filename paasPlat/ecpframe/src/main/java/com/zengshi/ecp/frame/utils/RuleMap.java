/** 
 * Date:2015年7月28日下午5:07:53 
 * 
*/
package com.zengshi.ecp.frame.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.zengshi.ecp.frame.context.EcpFrameContextHolder;

/** 
 * Description: <br>
 * Date:2015年7月28日下午5:07:53  <br>
 * 
 * @since JDK 1.6 
 * @see       
 */
public class RuleMap<K, V> extends HashMap<K, V> {

    /** 
     * serialVersionUID:TODO(用一句话描述这个变量表示什么). 
     * @since JDK 1.6 
     */ 
    private static final long serialVersionUID = 8851210363756872559L;
    
  
    public RuleMap(Map<K, V> map){
        super(map);
    }
}

