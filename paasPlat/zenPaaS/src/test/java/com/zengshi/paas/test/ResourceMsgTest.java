/** 
 * Date:2015-7-30下午3:27:34 
 * 
 */ 
package com.zengshi.paas.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.zengshi.paas.utils.ResourceMsgUtil;


/**
 * Description: <br>
 * Date:2015-7-30下午3:27:34  <br>
 * 
 * @version  
 * @since JDK 1.6 
 */
public class ResourceMsgTest {

    /**
     * Test method for {@link com.zengshi.paas.client.utils.ResourceMsgUtil#getMessage(java.lang.String, java.lang.Object[], java.util.Locale)}.
     */
    @Test
    public void testGetMessageStringObjectArrayLocale() {
        //fail("Not yet implemented");
        System.out.println(ResourceMsgUtil.getMessage("paas.test", null));
    }

    /**
     * Test method for {@link com.zengshi.paas.client.utils.ResourceMsgUtil#getMessage(java.lang.String, java.lang.Object[])}.
     */
    /*@Test
    public void testGetMessageStringObjectArray() {
        //fail("Not yet implemented");
        System.out.println(ResourceMsgUtil.getMessage("demo.test", null));
    }*/

}

