/**
 * 
 */
package com.zengshi.ecp.frame.test;


import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:ecp-frame-context.xml")
public class BaseTest extends AbstractJUnit4SpringContextTests{
	
	public void testDo(){
		
	}
	

}
