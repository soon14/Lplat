/**
 * 
 */
package com.zengshi.butterfly.core.config.provider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class DataBaseConfigProvider extends  AbstractConfigProvider{
	
	public static Log logger = LogFactory.getLog(DataBaseConfigProvider.class);
	
	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.provider.AbstractConfigProvider#loadConfigs()
	 */
	@Override
	protected  void loadConfigs() {
		//从数据库comframe的cfg_app_property表中取出有效数据（cfg_app_property.status=1），存入配置中心
		/*
		logger.info("init connect database comframe.cfg_app_property and try to get records....");
		try{
			ICfgAppPropertySV demoService=ServiceProvider.instance(ICfgAppPropertySV.class);
			CfgAppPropertyBean sqlBean=new CfgAppPropertyBean();
			//init sqlBean and execute query.the sqlBean="select * from comframe.cfg_app_property where status=1"
			sqlBean.setStatus(1);
			List<CfgAppPropertyBean> results=demoService.query(sqlBean);
			Iterator<CfgAppPropertyBean> it=results.iterator();
			CfgAppPropertyBean temp=null;
			while(it.hasNext()){
				temp=it.next();
				this.addConfig(new StringConfig(temp.getCfgKey(),temp.getCfgValue()));
			}
			logger.info("connect database comframe.cfg_app_property inited .And the get records operation succeed....");
		}
		catch(Exception e){
		  e.printStackTrace();	
		}
		*/
	}
	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.provider.AbstractConfigProvider#getLogger()
	 */
	@Override
	public Log getLogger() {
		return logger;
	}
	
	
}
