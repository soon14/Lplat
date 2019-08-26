/** 
 * Date:2015年8月18日下午4:32:35 
 * 
*/
package com.zengshi.paas.utils;

import com.zengshi.paas.cache.remote.RemoteCacheSVC;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 
 * Description: 缓存工具类<br>
 * Date:2015年8月18日下午4:32:35  <br>
 * 
 * @since JDK 1.6 
 * @see       
 */
public class CacheUtil {
    
    private static  RemoteCacheSVC cacheSVC;
    
    static{
        cacheSVC=PaasContextHolder.getContext().getBean("cacheSvc", RemoteCacheSVC.class);
    }
    
    public static RemoteCacheSVC getRemoteCacheSVC(){
        
        return cacheSVC;
    }
    /**
     * 
     * addItemToList:List缓存方式. <br/> 
     * 
     * @param key
     * @param object 
     * @since JDK 1.6
     */
    public static void addItemToList(String key, Object object){
        
        cacheSVC.addItemToList(key, object);
    }
    /**
     * 
     * getItemFromList:从List缓存中取数据. <br/> 
     * 
     * @param key
     * @return 
     * @since JDK 1.6
     */
    @SuppressWarnings("rawtypes")
    public static List getItemFromList(String key){
        
        return cacheSVC.getItemFromList(key);
    }
    /**
     * 
     * addItem:缓存对象. <br/> 
     * 
     * @param key
     * @param object 
     * @since JDK 1.6
     */
    public static void addItem(String key, Object object){
        cacheSVC.addItem(key, object);
    }

    /**
     * addItemNotExists 缓存对象，仅当 key 不存在的时候，才能写入；
     * @param key
     * @param object
     * @return 如果 key 存在，则不写入，返回 false;
     *    如果 key 不存在，写入成功，返回 true;
     */
    public static boolean addItemNotExists(String key, Object object){
        return cacheSVC.addItemNotExists(key ,object);
    }
    /**
     * 
     * addItem:存活期缓存. <br/> 
     * 
     * @param key
     * @param object
     * @param seconds 
     * @since JDK 1.6
     */
    public static void addItem(String key, Object object, int seconds){
        
        cacheSVC.addItem(key, object, seconds);
    }
    /**
     * 
     * addItemInt:自增长 <br/> 
     * 
     * @param key
     * @param object 
     * @since JDK 1.6
     */
    public static void addItemInt(String key,Long object){
        
        cacheSVC.addItemInt(key, object);
    }
    /**
     * 
     * flushDB:Delete all the keys of the currently selected DB. <br/> 
     * 
     * @return 
     * @since JDK 1.6
     */
    public static String flushDB(){
        
        return cacheSVC.flushDB();
    }
    /**
     * 
     * exists:判断缓存是否存在. <br/> 
     * 
     * @param key
     * @return 
     * @since JDK 1.6
     */
    public static boolean exists(String key){
        
        return cacheSVC.exists(key);
    }
    /**
     * 
     * keys:返回匹配key的缓存值. <br/> 
     * 
     * @param keyPattern
     * @return 
     * @since JDK 1.6
     */
    @SuppressWarnings("rawtypes")
    public static List keys(String keyPattern){
        
        return cacheSVC.keys(keyPattern);
    }
    /**
     * 
     * getItem:获取缓存值. <br/> 
     * 
     * @param key
     * @return 
     * @since JDK 1.6
     */
    public static Object getItem(String key){
        
        return cacheSVC.getItem(key);
    }
    /**
     * 
     * delItem:删除缓存值. <br/> 
     * 
     * @param key 
     * @since JDK 1.6
     */
    public static void delItem(String key){
        
        cacheSVC.delItem(key);
    }
    /**
     * 
     * getIncrement:获取自增长缓存值. <br/> 
     * 
     * @param key
     * @return 
     * @since JDK 1.6
     */
    public static long getIncrement(String key){
        
        return cacheSVC.getIncrement(key);
    }
    /**
     * 
     * getDecrement:获取自减缓存值. <br/> 
     * 
     * @param key
     * @return 
     * @since JDK 1.6
     */
    public static long getDecrement(String key){
        
        return cacheSVC.getDecrement(key);
    }
    /**
     * 
     * setHashMap:缓存HashMap对象. <br/> 
     * 
     * @param key
     * @param map 
     * @since JDK 1.6
     */
    public static void setHashMap(String key, HashMap<String, String> map){
        
        cacheSVC.setHashMap(key, map);
    }
    /**
     * 
     * getHashMap:获取HashMap缓存对象. <br/> 
     * 
     * @param key
     * @return 
     * @since JDK 1.6
     */
    public static Map<String, String> getHashMap(String key){
        
        return cacheSVC.getHashMap(key);
    }

    /**
     * 
     * hsetItem:指定字段的哈希值. <br/> 
     * 
     * @param key
     * @param field
     * @param object 
     * @since JDK 1.6
     */
    public static void hsetItem(String key, String field,Object object){
        
        cacheSVC.hsetItem(key, field, object);
    }
    /**
     * 
     * hgetItem:获取指定字段哈希值. <br/> 
     * 
     * @param key
     * @param field
     * @return 
     * @since JDK 1.6
     */
    public static Object hgetItem(String key, String field){
        
        return cacheSVC.hgetItem(key, field);
    }
    /**
     * 
     * hdelItem:删除字段哈希值. <br/> 
     * 
     * @param key
     * @param field 
     * @since JDK 1.6
     */
    public static void hdelItem(String key, String field){
        
        cacheSVC.hdelItem(key, field);
    }
    /**
     * 
     * expire:设置缓存过期时间(秒). <br/> 
     * 
     * @param key
     * @param seconds 
     * @since JDK 1.6
     */
    public static void expire(String key,int seconds){
        
        cacheSVC.expire(key, seconds);
    }
    /**
     * 
     * hkeys:返回哈希Field. <br/> 
     * 
     * @param key
     * @return 
     * @since JDK 1.6
     */
    public static Set<String> hkeys(String key){
        
        return cacheSVC.hkeys(key);
    }
    
    /**
     * 
     * saddItem: 将 object 加入到 key对应的 set中 <br/> 
     * 
     * @param key
     * @param object 
     * @since JDK 1.6
     */
    public static void saddItem(String key,Object object){
        cacheSVC.saddItem(key, object);
    }

    /**
     *
     * saddStringItem: 将 String 加入到 key对应的 set中 <br/>
     *
     * @param key
     * @param member
     */
    public static void saddStringItem(String key,String member){
        cacheSVC.saddStringMember(key,member);
    }
    /**
     *
     * addSet:缓存Set对象. <br/>
     *
     * @param key
     * @param set
     * @since JDK 1.6
     */
    public static void addSet(String key, Set<String> set){

        cacheSVC.addSet(key, set);
    }
    /**
     *
     * getSet:获取Set缓存对象. <br/>
     *
     * @param key
     * @return
     * @since JDK 1.6
     */
    public static Set<String> getSet(String key){

        return cacheSVC.getSet(key);
    }

    /**
     * 判断 member 是否是 key 的集合的元素；
     * @param key
     * @param member
     * @return
     */
    public static boolean sIsMember(String key, String member){
        return cacheSVC.isSetMember(key, member);
    }

    /**
     * 对 keys的集合取并集；
     * @param keys
     * @return
     */
    public static Set<String> sUnion(String... keys){
        return cacheSVC.sUnion(keys);
    }

    /**
     * 对 key 对应的集合删除 member 节点；
     * @param key
     * @param member
     */
    public static void sRemMember(String key , String member){
         cacheSVC.sRemMember(key, member);
    }

    public static void addItemFile(String key, byte[] file) {
        cacheSVC.addItemFile(key, file);
    }

    public static Set<String> getSetForStatic(String key) {
        return cacheSVC.getSet(key);
    }
    
    public static void zaddItem(String key ,String item , double score){
        cacheSVC.zaddItem(key, item, score);
    }

    public static Long zdelItem(String key,String item) {
        return cacheSVC.zdelItem(key,item);
    }

    /**
     * 获取有序集合的下标
     * @param key
     * @param item
     * @return
     */
    public static Long zrank(String key,String item) {
        return cacheSVC.zrank(key,item);
    }

    public static Set<String> zgetItems(String key){
        return cacheSVC.zgetItems(key);
    }

    public static Double zgetScore(String key,String item) {
        return cacheSVC.zgetScore(key, item);
    }

    public static Double zincrby(String key,String item,double score) {
        return cacheSVC.zincrby(key, item, score);
    }

    /**
     * 设置某个时间点过期
     * @param key
     * @param unixTime 1970后毫秒
     */
    public static void expireAt(String key,long unixTime){
        cacheSVC.expireAt(key, unixTime);
    }

}

