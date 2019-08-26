package com.zengshi.paas.test;

import com.zengshi.paas.lock.DistributeLock;
import com.zengshi.paas.utils.CacheUtil;
import org.junit.Test;

/**
 */
public class DistributeLockTest {

    @Test
    public void testLock(){
        String key = "ss";
        DistributeLock lock = DistributeLock.lock(key);
        if(lock == null){
            System.out.println("=========木有锁===========");
        } else {
            System.out.println("======="+lock.toJsonString());
        }
    }
    @Test
    public void testLock2(){
        String key = "test2";
        DistributeLock lock = DistributeLock.lock(key);
            System.out.println("======="+lock.toJsonString());
        lock.clearLock();
        Object obj = CacheUtil.getItem("dis.lock."+key);
        if(obj == null){
            System.out.println("====锁清理了");
        }else {
            System.out.println("====锁还在");
        }
    }

    @Test
    public void testLockAutoClear() throws Exception{
        String key = "test3";
        DistributeLock lock = DistributeLock.lock(key,10);
        System.out.println("======="+lock.toJsonString());

        Object obj = CacheUtil.getItem("dis.lock."+key);
        if(obj == null){
            System.out.println("====锁清理了");
        }else {
            System.out.println("====锁还在"+(String)obj);
        }

        Thread.sleep(11*1000);

        lock = DistributeLock.lock(key,50);
        System.out.println("======="+lock.toJsonString());
    }
}
