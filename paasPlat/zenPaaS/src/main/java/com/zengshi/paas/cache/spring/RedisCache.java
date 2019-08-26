/** 
 * Date:2015年8月17日下午5:47:50 
 * 
*/
package com.zengshi.paas.cache.spring;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import com.zengshi.paas.cache.remote.RemoteCacheSVC;
import com.zengshi.paas.utils.CacheUtil;

/** 
 * Description: 与spring cache结合<br>
 * Date:2015年8月17日下午5:47:50  <br>
 * 
 * @since JDK 1.6 
 * @see       
 */
public class RedisCache implements Cache {
    
    private RemoteCacheSVC cache=CacheUtil.getRemoteCacheSVC();
    
    private String name;
    
    public void setCache(RemoteCacheSVC cache) {
        this.cache = cache;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this.cache;
    }

    @Override
    public ValueWrapper get(Object key) {
        
        Object value=this.cache.getItem(String.valueOf(key));
        return null!=value?new SimpleValueWrapper(value):null;
    }

    @Override
    public void put(Object key, Object value) {
        
        this.cache.addItem(String.valueOf(key), value);
    }

    @Override
    public void evict(Object key) {
        
        this.cache.delItem(String.valueOf(key));
    }

    @Override
    public void clear() {
        
        this.cache.flushDB();
    }

}

