package com.zengshi.paas.test;

import com.zengshi.paas.utils.CacheUtil;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 */
public class CacheUtilsTest {

    private static final String KEY1="XHS_PKG_CATG_001";

    private static final String KEY2="XHS_PKG_CATG_002";

    @org.junit.BeforeClass
    public static void cleanCache(){
        System.out.println("=======清理key======");
        CacheUtil.delItem(KEY1);
        CacheUtil.delItem(KEY2);
    }

    @Test
    public void setTest(){

        Set<String> tmp = new HashSet<String>();
        tmp.add("a");
        tmp.add("x");
        tmp.add("112");
        System.out.println("========key1加了set====");
        CacheUtil.addSet(KEY1,tmp);
        printSet(CacheUtil.getSet(KEY1));

        System.out.println("========key1 加了110====");
        CacheUtil.saddStringItem(KEY1,"110");
        printSet(CacheUtil.getSet(KEY1));

        System.out.println("========key2====");
        CacheUtil.saddStringItem(KEY2,"120");
        CacheUtil.saddStringItem(KEY2,"110");
        printSet(CacheUtil.getSet(KEY2));

        System.out.println("========并集====");
        printSet(CacheUtil.sUnion(KEY1,KEY2));

        System.out.println("========删除集合key1 的 x 节点====");
        CacheUtil.sRemMember(KEY1,"x");
        printSet(CacheUtil.getSet(KEY1));

        System.out.println("========删除之后的并集====");
        printSet(CacheUtil.sUnion(KEY1,KEY2));

        CacheUtil.delItem(KEY1);
        printSet(CacheUtil.getSet(KEY1));
    }

    @Test
    public void testRedisMisc() {
        String strTmp = (String) CacheUtil.hgetItem("aaaa","bbbb");
        if (null == strTmp) {
            System.out.println("strTmp is null");
        } else {
            System.out.println(strTmp);
        }
        CacheUtil.hsetItem("mydev.test","hello",1);
        System.out.println(CacheUtil.hgetItem("mydev.test", "hello"));
        CacheUtil.hsetItem("mydev.test","hello",3);
        System.out.println(CacheUtil.hgetItem("mydev.test","hello"));
    }
    @Test
    public void testZMethods() {
        System.out.println("========测试z系列方法====");
        CacheUtil.zaddItem("MyTest","hello",1);
        CacheUtil.zaddItem("MyTest","hello2",2);
        CacheUtil.zaddItem("MyTest","hello3",3);
        System.out.println(CacheUtil.zrank("MyTest","hello"));
        System.out.println(CacheUtil.zrank("MyTest","hello2"));
        System.out.println(CacheUtil.zrank("MyTest","hello333"));
        CacheUtil.zincrby("MyTest","hello2",5);
        System.out.println(CacheUtil.zgetScore("MyTest","hello2").intValue());
    }

    private void printSet(Set<String> sets){
        for(String member : sets){
            System.out.print(member+"  ");
        }
        System.out.println("");
    }


}
