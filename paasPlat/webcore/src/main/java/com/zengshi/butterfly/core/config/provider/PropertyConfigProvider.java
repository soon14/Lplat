/**
 * 
 */
package com.zengshi.butterfly.core.config.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import com.zengshi.butterfly.core.config.Config;
import com.zengshi.butterfly.core.config.StringConfig;


public class PropertyConfigProvider extends AbstractConfigProvider{
	
	public static Log logger = LogFactory.getLog(PropertyConfigProvider.class);
	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.provider.AbstractConfigProvider#getLogger()
	 */
	@Override
	public Log getLogger() {
		return logger;
	}
	/**
	 * 属性文件列表
	 */
	private  List<String> propertyFilePaths = new ArrayList<String>();
	
	
	
	public PropertyConfigProvider() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PropertyConfigProvider(String propertyfile) {
		super();
		if(propertyfile != null) {
			this.propertyFilePaths.add(propertyfile);
		}
	}
	
	public PropertyConfigProvider(String[] propertyfiles) {
		super();
		// TODO Auto-generated constructor stub
		if(propertyfiles != null) {
			for(String file:propertyfiles) {
				this.propertyFilePaths.add(file);
			}
		}
		
	}




	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.provider.AbstractConfigProvider#loadConfigs()
	 */
	@Override
	public void loadConfigs() {
		
		try {
			logger.info("start init PropertyConfigProvider....");
			InputStream ins=Thread.currentThread().getContextClassLoader().getResourceAsStream("propertiesFiles.xml");
			if(ins != null) {
				Digester digester = new Digester();
				addRules(digester);
				digester.parse(Thread.currentThread().getContextClassLoader().getResourceAsStream("propertiesFiles.xml"));
			}
			
			this.configs = new HashMap<String,Config>();
			
			if(propertyFilePaths != null){
				for(String path: propertyFilePaths){
					Map<String, Config> perFileConfig = new HashMap<String, Config>();
					
					Properties properties = new Properties();
					InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
					properties.load(is);
					
					Iterator iterator =  properties.stringPropertyNames().iterator();
					while(iterator.hasNext()){
						String key = (String) iterator.next();
						Config aConfig = new StringConfig(key,properties.getProperty(key));
						perFileConfig.put(aConfig.getKey(), aConfig);
					}
					this.putAll(perFileConfig);
				}
			}
			
			this.isInited = true;
			logger.info("PropertyConfigProvider inited....");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	/**
	 * 添加配置信息
	 * @param newConfig
	 * @throws ServiceException,不允许出现key值相同的配置
	 */
	private void putAll(Map<String,Config> newConfig ){
		
		if(this.configs==null){
			this.configs = new HashMap<String, Config>();
		}
		
		if(configs.isEmpty()){
			configs.putAll(newConfig);
			return;
		}
		
		Iterator<String> iterator = newConfig.keySet().iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			if(configs.containsKey(key)){
				logger.warn("存在冲突的key值: "+key+"，请修改");
			}
		}
		configs.putAll(newConfig);
	}
	/**
	 * 添加文件规程
	 * @param digester
	 */
	private static void addRules(Digester digester) {
		digester.addObjectCreate("propertyFiles-config", PropertyConfigProvider.class);
		digester.addCallMethod("propertyFiles-config/propertyFiles/add-file","addFile", 1);
		digester.addCallParam("propertyFiles-config/propertyFiles/add-file", 0,"path");
	}
	
	/**
	 * 添加配置文件
	 * @param path
	 */
	public  void addFile(String path){
		this.propertyFilePaths.add(path);
	}
	
	
}
