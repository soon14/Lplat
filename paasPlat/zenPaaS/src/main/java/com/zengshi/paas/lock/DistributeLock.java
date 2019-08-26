package com.zengshi.paas.lock;

import com.zengshi.paas.utils.CacheUtil;

import java.io.Serializable;
import java.util.UUID;

/**
 * 分布式锁； 利用 Redis的特性形成分布式锁；
 */
public class DistributeLock implements Serializable{

    private static final long serialVersionUID = 3511445940741080355L;

    //锁的键；
   private String key ;

    //当前值；针对锁的值；
    private String localValue;

    //超时时间；时间单位秒；
    private int expireTime;

    //超时方式；设定超时时间，或者是截止某个时间的超时；默认为超时时间；
    private String expireType;

    //默认的超时时间是 5分钟；
    private static final int DEFAULT_EXPIRTE_TIME = 5*60;

    //超时方式，时间超时
    public static final String EXPIRTE_TYPE_TIME = "1";

    //超时方式：截止时间超时；
    public static final String EXPIRTE_TYPE_AT = "2";

    private static final String KEY_PREFIX = "dis.lock.";

    /**
     * 分布式锁； 根据Key，获取锁；如果返回值为空，表示没获取到锁；否则返回锁的信息；
     * 返回的锁信息，通过调用clear 信息，释放锁；
     * @param key
     * @return
     */
    public static DistributeLock lock(String key){
        //默认锁 5分钟；
        return DistributeLock.lock(key, DEFAULT_EXPIRTE_TIME);
    }

    /**
     * 分布式锁； 根据Key， 超时时间； 获取锁；如果返回值为空，表示没获取到锁；否则返回锁的信息；
     * 返回的锁信息，通过调用clear 信息，释放锁；
     * @param key
     * @param expireTime 超时时间，到了这个时间，锁自动释放；秒为单位；
     * @return
     */
    public static DistributeLock lock(String key, int expireTime){
        if(key == null || key.length() ==0){
            return null;
        }
        DistributeLock lock = new DistributeLock(key, expireTime);
        //锁的值；
        lock.localValue = UUID.randomUUID().toString();
        //使用 addItenNotExists 达到唯一写入的目的；
        boolean isOk = CacheUtil.addItemNotExists(lock.key,lock.localValue);
        if(isOk){
            CacheUtil.expire(lock.key, lock.expireTime);
            return lock;
        } else {
            return null;
        }
    }

    /**
     * 初始化锁；
     * @param key
     * @param expireTime
     */
    private DistributeLock(String key, int expireTime){
        this.key = KEY_PREFIX + key;
        if(expireTime < 1){
            this.expireTime = DEFAULT_EXPIRTE_TIME;
        } else {
            this.expireTime = expireTime;
        }
        this.expireType = EXPIRTE_TYPE_TIME;
    }

    /**
     * 根据Key，时间，超时类型，生成分布式锁；
     * 如果类型为空，或者 不是 1， 也不是2 ；那么该值设置为 1；
     * @param key
     * @param expireTime
     * @param expireType
     */
    private DistributeLock(String key, int expireTime, String expireType){
        this.key = KEY_PREFIX  + key;
        this.expireTime = expireTime;

        //为空，或者 不等于 1 ，或者 不等于2；
        if(expireType == null || !DistributeLock.EXPIRTE_TYPE_TIME.equalsIgnoreCase(expireType)
            || !DistributeLock.EXPIRTE_TYPE_AT.equalsIgnoreCase(expireType)){
            this.expireType = EXPIRTE_TYPE_TIME;
        } else {
            this.expireType = expireType;
        }
    }

    /**
     * 释放锁；
     */
    public void clearLock(){
        if(this.key == null || this.key.length() ==0){
            return ;
        }
        Object objValue = CacheUtil.getItem(this.key);
        if(objValue == null){
            return ;
        }
        String value = (String)objValue;
        //缓存的值和锁的值一样，那么删除锁；（释放锁）；
        if(this.localValue.equalsIgnoreCase(value)){
            CacheUtil.delItem(this.key);
        }
    }

    public String toJsonString(){
        return "{"
            + "'key':'"+this.key+"',"
            + "'localValue':'"+this.localValue+"',"
            + "'expireTime':'"+this.expireTime+"',"
            + "}";
    }

}
