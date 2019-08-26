/**
 * 
 */
package com.zengshi.butterfly.core.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zengshi.butterfly.core.config.center.CenterPriorityEnum;
import com.zengshi.butterfly.core.config.center.ConfigCenter;
import com.zengshi.butterfly.core.config.center.ConfigCenterFactory;

/**
 * 默认的ConifgContainer实现类
 * 2011-7-22
 */
public class ConfigContainerDefault implements ConfigContainer {

	private static final Log logger = LogFactory.getLog(ConfigContainerDefault.class);
	
	
	/**
	 * 所有的配置中心
	 */
	private List<ConfigCenter> configCenterStack = new ArrayList<ConfigCenter>();
	
	
	/**
	 * ConfigContainerDefault 的实例化采用单例模式，用来引用
	 */
	private static ConfigContainer configContainer;
	
	
	
	
	/**
	 * 私有构造方法
	 * @throws IOException
	 */
	private ConfigContainerDefault() throws IOException{
		
	}
	
	/**
	 * 获取ConfigContainerDefault的实例
	 * @return
	 * @throws Exception 
	 */
	public static ConfigContainer getInstance() throws Exception{
		
		if(configContainer == null){
			configContainer = new ConfigContainerDefault();
			for(CenterPriorityEnum centerPriorityEnum : CenterPriorityEnum.values()){
				configContainer.addConfigCenter(ConfigCenterFactory.getConfigCenter(centerPriorityEnum));
			}
			((ConfigContainerDefault)configContainer).sortCenters();
		}
		
		return configContainer;
	}
	
	
	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.temp.ConfigContainer#addConfig(int, int)
	 */
	@Override
	public void addConfig(CenterPriorityEnum centerPriority,Config config) {
		
		for(ConfigCenter aCenter :this.configCenterStack){
			if(aCenter.getPriority() == centerPriority.getPriority()){
				aCenter.addConfig(config);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.temp.ConfigContainer#addConfigCenter(com.zengshi.cu.config.temp.ConfigCenter)
	 */
	@Override
	public boolean addConfigCenter(ConfigCenter configCenter) {
		
		boolean existSamePriorityCenter = false;//存在相同优先级的配置中心
		for(ConfigCenter aCenter :this.configCenterStack){
			if(aCenter.getPriority() == configCenter.getPriority()){
				existSamePriorityCenter = true;
			}
		}
		if(existSamePriorityCenter){
			logger.error("存在相同优先级的配置configCenter");
			return false;
		}else{
			//不存在相同优先级的配置中心
			this.configCenterStack.add(configCenter);
			this.sortCenters();
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.temp.ConfigContainer#clear()
	 */
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.temp.ConfigContainer#findConfigCenter(int)
	 */
	@Override
	public ConfigCenter findConfigCenter(CenterPriorityEnum centerPriority) {
		ConfigCenter configCenter = null;
		for(ConfigCenter aCenter :this.configCenterStack){
			if(aCenter.getPriority() == centerPriority.getPriority()){
				configCenter = aCenter;
			}
		}
		return configCenter;
	}

	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.temp.ConfigContainer#findeConfig(java.lang.String)
	 */
	@Override
	public Config findConfig(String key) {
		if(key == null || "".equals(key)){
			return null;
		}
		
		Config config = null;
		String debugInfo ="";
		for(ConfigCenter aCenter: this.sortCenters()){
			config = aCenter.getConfig(key);
			if(config != null){
				debugInfo = "在中心："+aCenter.getClass()+"中找到配置。key为："+key+" value为:"+ config;
				break;
			}
		}
		
		if(config != null){
			logger.debug(debugInfo);
		}else{
			logger.debug("没有找到key为："+key+"的配置");
		}
		
		return config;
	}

	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.temp.ConfigContainer#findeConfig(java.lang.String, int)
	 */
	@Override
	public Config findConfig(String key, CenterPriorityEnum centerPriority) {
		ConfigCenter configCenter = this.findConfigCenter(centerPriority);
		if(configCenter == null){
			logger.warn("没有找到对应优先级的configCenter");
			return null;
		}
		Config config = configCenter.getConfig(key);
		if(config != null){
			logger.debug("在中心："+configCenter.getClass()+"中找到配置。key为："+key+" value为:"+ config);
		}else{
			logger.debug("在中心："+configCenter.getClass()+"中没找到key为："+key+"配置。");
		}

		return config;
	}

	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.temp.ConfigContainer#removeConfig(int, int)
	 */
	@Override
	public void removeConfig(CenterPriorityEnum centerPriority, Config config) {
		ConfigCenter configCenter = this.findConfigCenter(centerPriority);
		if(configCenter == null){
			logger.warn("没有找到对应优先级的configCenter");
			return;
		}
		if(config == null || config.getKey() == null){
			return;
		}
		configCenter.removeConfig(config.getKey());
	}

	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.temp.ConfigContainer#reset()
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.temp.ConfigContainer#reset(int)
	 */
	@Override
	public void reset(CenterPriorityEnum centerPriority) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.temp.Injectable#injectConfig(java.lang.Class)
	 */
	@Override
	public <T> T injectConfig(Class<T> clazz) {
		
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}


	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.temp.Injectable#injectConfig(java.lang.Object)
	 */
	@Override
	public <T> boolean injectConfig(T t) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	/**
	 * 将中心列表按优先级排序
	 * @return 返回按优先级排序后的中心列表
	 */
	private List<ConfigCenter> sortCenters(){
		Collections.sort(this.configCenterStack, new Comparator<ConfigCenter>(){
			@Override
			public int compare(ConfigCenter center1, ConfigCenter center2) {
				return center1.getPriority() - center2.getPriority() >0 ? -1:1;
			}
		});
		return this.configCenterStack;
	}
}
