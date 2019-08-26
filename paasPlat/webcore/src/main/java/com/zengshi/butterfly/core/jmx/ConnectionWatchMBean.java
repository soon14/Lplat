/**
 * 
 */
package com.zengshi.butterfly.core.jmx;

/**
 *
 */
public interface ConnectionWatchMBean {

	public int activeConnection();
	
	public int idleConnection();
	
	public void appendToFile(boolean start);
}
