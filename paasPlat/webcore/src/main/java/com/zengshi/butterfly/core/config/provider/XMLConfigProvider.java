/**
 * 
 */
package com.zengshi.butterfly.core.config.provider;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.zengshi.butterfly.core.config.Config;
import com.zengshi.butterfly.core.config.StringConfig;


public class XMLConfigProvider extends AbstractConfigProvider{
	
	public static Log logger = LogFactory.getLog(XMLConfigProvider.class);
	
	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.provider.AbstractConfigProvider#getLogger()
	 */
	@Override
	public Log getLogger() {
		return logger;
	}
	
	private void addConfig(String key, Config config){
		if(configs == null){
			configs = new HashMap<String,Config>();
		}
		if(configs.containsKey(key)){
			logger.warn("存在冲突的key值: "+key+"，请修改");
		}else{
			configs.put(key, config);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.zengshi.cu.config.provider.AbstractConfigProvider#loadConfigs()
	 */
	@Override
	protected void loadConfigs() {
		
		logger.info("start load XMLConfigProvider's data....");
		try {
			this.configs = new HashMap<String,Config>();
			
			Document document = new SAXReader().read(Thread.currentThread().getContextClassLoader().getResourceAsStream("configs.xml")); 
			addConfigsFromElement(document.getRootElement());
			
			logger.info("XMLConfigurationProvider data loaded....");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 充Element元素里获取配置项添加到this.configs
	 * @param element
	 * @throws ServiceException 
	 */
	private void addConfigsFromElement(Element element){
		Iterator<Element> iterator = element.elementIterator();
		while(iterator.hasNext()){
			Element ele = iterator.next();
			if(ele.elementIterator().hasNext() && !"".equals(ele.getText().trim())){
				StringConfig aConfig = new StringConfig(ele.getPath().substring("/configurations/".length()).replaceAll("/", "."),ele.getText().trim());
				addConfig(aConfig.getKey(), aConfig);
			}
			if(!ele.elementIterator().hasNext()){
				StringConfig aConfig = new StringConfig(ele.getPath().substring("/configurations/".length()).replaceAll("/", "."),ele.getText().trim());
				addConfig(aConfig.getKey(), aConfig);
			}
			if(ele.elementIterator().hasNext()){
				addConfigsFromElement(ele);
			}
		}
	}
}
