/** 
 * Date:2015-8-10下午3:12:59 
 * 
 */ 
package com.zengshi.paas.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.zengshi.paas.utils.LogUtil;

/**
 * Description: <br>
 * Date:2015-8-10下午3:12:59  <br>
 * 
 * @version  
 * @since JDK 1.6 
 */
public class LogUtilTest {
    
    ///定义个常量，用于表示模块名称，可以使用：当前类的类名
    private static final String MODULE = LogUtilTest.class.getName();

    @Test
    public void testErrorStringString() {
        ///异常级别日志输出
        LogUtil.error(MODULE, "测试的消息哦");
    }
    
    @Test
    public void testError(){
        Exception err = new Exception("测试一个异常消息");
        ///异常级别日志输出，同时会将exception输出；
        LogUtil.error(MODULE, "异常" , err);
    }
}

