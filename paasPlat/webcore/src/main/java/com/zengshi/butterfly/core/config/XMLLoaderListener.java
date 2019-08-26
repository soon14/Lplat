package com.zengshi.butterfly.core.config;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.context.ServletContextAware;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.zengshi.butterfly.core.web.spring.PropertyLoadListener;

public class XMLLoaderListener implements PropertyLoadListener,ServletContextAware{

	private Resource resource;
	
	private ResourcePatternResolver resourceResolver=new PathMatchingResourcePatternResolver();
	
	private static final String DEFAULT_FILE="classpath*:butterfly-application-default.xml";

	@Override
	public void beforePropertyLoad(Properties props) {
	}

	private void readKey(Element childElement,String parentKey,Map<String, String> property) {
		
		List<Element> childrend=DomUtils.getChildElements(childElement);
		if(childrend != null && childrend.size() >0) {
			for(Element ele:childrend) {
				this.readKey(ele,parentKey+"."+ele.getNodeName(),property);
			}
		}else {
			String key=childElement.getAttribute("key");
			if(key != null && !"".equals(key.trim())) {
				parentKey +="."+key;
			}
			String value=childElement.getAttribute("value");
			if(value == null || "".equals(value)) {
				value=DomUtils.getTextValue(childElement);
			}
			if(value != null && !"".equals(value.trim())) {
				property.put(parentKey, value);
			}
		}
	
	}
	@Override
	public void afterPropertyLoad(Properties props) {
		try {
			Resource[] defaultFile=this.resourceResolver.getResources(DEFAULT_FILE);
			for(Resource r:defaultFile) {
				loadResource(r,props);
			}
			loadResource(this.resource,props);
			Application.addProperty(props);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private void loadResource(Resource resource,Properties props) throws Exception {
		InputStream is=null;
		try {
			is=resource.getInputStream();
		
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(is);
	
			Element root = doc.getDocumentElement(); 
			
			
			List<Element> children=DomUtils.getChildElements(root);
	
			Map<String, String> property=new HashMap<String, String>();
	
			for(Element ele:children) {
				String key=ele.getNodeName();
				if(ele.getNodeName().equals("import")) {
					String importFile=DomUtils.getTextValue(ele);
					Resource[] importResource=resourceResolver.getResources(importFile);
					if(importResource != null && importResource.length >0) {
						for(Resource r:importResource) {
							this.loadResource(r, props);
						}
					}
				}else {
					this.readKey(ele,key,property);
				}
				
			}
			synchronized (props) {
				props.putAll(property);
			}
		} catch (Exception e) {
			//throw e;
			e.printStackTrace();
			System.exit(0);
		}finally {
			if(is != null) is.close();
		}
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		// TODO Auto-generated method stub
		//this.resourceResolver=new ServletContextResourcePatternResolver(servletContext); 
		this.resourceResolver =new PathMatchingResourcePatternResolver();
	}

}
