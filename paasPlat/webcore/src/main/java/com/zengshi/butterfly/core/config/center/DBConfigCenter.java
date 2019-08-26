/**
 * 
 */
package com.zengshi.butterfly.core.config.center;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 系统中通过DB的方式的配置信息 保存在该配置中心
 * 2011-7-15
 */
public class DBConfigCenter extends AbstractConfigCenter {
	private static final Log log= LogFactory.getLog(DBConfigCenter.class); 
	
	DBConfigCenter(){
		this.priority = CenterPriorityEnum.DB.getPriority();
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
