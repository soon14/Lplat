/**
 * 
 */
package com.zengshi.butterfly.core.config.center;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 系统中通过应用的配置文件的的方式的配置信息 保存在该配置中心
 * 2011-7-15
 */
public class CustomerConfigCenter extends AbstractConfigCenter {
	
	private static final Log log= LogFactory.getLog(CustomerConfigCenter.class); 
	
	CustomerConfigCenter(){
		this.priority = CenterPriorityEnum.CUSTORMER.getPriority();
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
