/**
 * 
 */
package com.zengshi.butterfly.core.config.center;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 开发过程中某些用来覆盖DB或者Customer中心的配置 保存在该配置中心
 * 2011-7-15
 */
public class DevConfigCenter extends AbstractConfigCenter {
	
	private static final Log log= LogFactory.getLog(DevConfigCenter.class); 
	
	DevConfigCenter(){
		this.priority = CenterPriorityEnum.DEV.getPriority();
	}
	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.center.AbstractConfigCenter#getLogger()
	 */
	@Override
	protected Log getLogger() {
		return log;
	}
	

	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.center.ConfigCenter#getPriority()
	 */
	@Override
	public int getPriority() {
		return this.priority;
	}
}
