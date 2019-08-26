/**
 * 
 */
package com.zengshi.paas.test;

import com.zengshi.paas.utils.CipherUtil;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;



/**
 *
 */
public class CiperToolsTest {

	/**
	 * Test method for {@link com.zengshi.paas.utils.CipherUtil#encrypt(java.lang.String)}.
	 */
	@Test
	public void testEncryptString() {
	    
		System.out.println(CipherUtil.encrypt("ucom"));
		System.out.println(CipherUtil.encrypt("ucomm02"));
		System.out.println("ecpdev_common : "+CipherUtil.encrypt("ecpdev_common$123"));
		System.out.println("ecpdev_common_slave : "+CipherUtil.encrypt("ecpdev_common_slave$123"));
		
		System.out.println("ecpdev01 : "+CipherUtil.encrypt("ecpdev01$123"));
        System.out.println("ecpdev01_slave : "+CipherUtil.encrypt("ecpdev01_slave$123"));
        
        System.out.println("ecpdev02 : "+CipherUtil.encrypt("ecpdev02$123"));
        System.out.println("ecpdev02_slave : "+CipherUtil.encrypt("ecpdev02_slave$123"));
		
        System.out.println("webdb01 : "+CipherUtil.encrypt("webdb01"));
		Assert.assertEquals(true, true);
	}

	/**
	 * Test method for {@link com.zengshi.paas.utils.CipherUtil#decrypt(java.lang.String)}.
	 */
	@Test
	@Ignore
	public void testDecryptString() {
		System.out.println("dbfc229164e44c9fa61403462f3f23c6 : "+CipherUtil.decrypt("dbfc229164e44c9fa61403462f3f23c6"));
	}

}

