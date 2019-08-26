package com.zengshi.butterfly.core.web.spring.resolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import com.zengshi.butterfly.core.config.Application;
import com.zengshi.butterfly.core.web.WebConstants;

/**
 * 可以根据当前传入的theme值，获取不同theme资源文件
 *
 */
public class ButterflyThemeResourceHandler extends ResourceHttpRequestHandler {
	
	private static final Log logger = LogFactory.getLog(ButterflyThemeResourceHandler.class);

	@Autowired
	private ThemeResolver resolver;
	
	private ResourcePatternResolver resourceResolver;  
	
	//private String domain;
	
	//private String module;
	
	protected DomainResolver domainResolver=new ApplicationDomainResolver();
	
	protected ModuleResolver moduleResolver=new ApplicationModuleResolver();
	
	private boolean inited=false;
	
	private Map<String,Resource[]> localtionThemeBundle=new HashMap<String, Resource[]>();
	
	//private boolean userCache=true;
	
	private Map<LocationInfo,Resource> locationCache=new HashMap<LocationInfo,Resource>();
	
	
	
	public ButterflyThemeResourceHandler() {
		super();
	}


	@Override
	protected Resource getResource(HttpServletRequest request) {
		// TODO Auto-generated method stub
		String themeName=resolver.resolveThemeName(request);
		if(!localtionThemeBundle.containsKey(themeName)) {
			List<String> resouceList=this.getResouceLocaltion(themeName,request);
			Resource[] locations=null;
			for(String _resouce:resouceList) {
				try {
					Resource[] resoucere=resourceResolver.getResources(_resouce);
					locations=(Resource[])ArrayUtils.addAll(locations, resoucere);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			localtionThemeBundle.put(themeName, locations);
			
		}
		return this.getThemeResource(request, localtionThemeBundle.get(themeName),themeName);
	}
	
	private List<String> getResouceLocaltion(String theme,HttpServletRequest request) {
		List<String> resouce=new ArrayList<String>();
		String[] path=StringUtils.commaDelimitedListToStringArray(Application.getValue(WebConstants.THEME_RESOURCE_PATH));
		for(String p:path) {
			resouce.add(p+"resources/domains/"+this.getDomain(request)+"/"+this.getModule(request)+"/"+theme+"/");
			resouce.add(p+"resources/domains/"+this.getDomain(request)+"/"+this.getModule(request)+"/default/");
			resouce.add(p+"resources/domains/global/"+theme+"/");
			resouce.add(p+"resources/domains/global/default/");
			resouce.add(p+"resources/global-modules/"+this.getModule(request)+"/"+theme+"/");
			resouce.add(p+"resources/global-modules/"+this.getModule(request)+"/default/");
			resouce.add(p+"resources/global-modules/global/"+theme+"/");
			resouce.add(p+"resources/global-modules/global/default/");
			resouce.add(p+"resources/global-theme/"+theme+"/");
			resouce.add(p+"resources/global-theme/default/");
		}
		
		
		/*resouce.add("classpath:resources/domains/"+this.getDomain(request)+"/"+this.getModule(request)+"/"+theme+"/");
		resouce.add("classpath:resources/domains/"+this.getDomain(request)+"/"+this.getModule(request)+"/default/");
		resouce.add("classpath:resources/domains/global/"+theme+"/");
		resouce.add("classpath:resources/domains/global/default/");
		resouce.add("classpath:resources/global-modules/"+this.getModule(request)+"/"+theme+"/");
		resouce.add("classpath:resources/global-modules/"+this.getModule(request)+"/default/");
		resouce.add("classpath:resources/global-modules/global/"+theme+"/");
		resouce.add("classpath:resources/global-modules/global/default/");
		resouce.add("classpath:resources/global-theme/"+theme+"/");
		resouce.add("classpath:resources/global-theme/default/");*/
		
		return resouce;
	}
	
	protected Resource getThemeResource(HttpServletRequest request,Resource[] locations,String theme) {
		String url=request.getRequestURI();
		String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		if (path == null) {
			throw new IllegalStateException("Required request attribute '" +
					HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE + "' is not set");
		}

		if (!StringUtils.hasText(path) || isInvalidPath(path)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Ignoring invalid resource path [" + path + "]");
			}
			return null;
		}
		if(this.useCache()) {
			LocationInfo themeResourceInfo=new LocationInfo(this.getDomain(request), this.getModule(request), theme, url);
			if(this.locationCache.containsKey(themeResourceInfo)) {
				return this.locationCache.get(themeResourceInfo);
			}
		}
		
		
		//TODO 增加缓存，提高命中率
		for (Resource location : locations) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Trying relative path [" + path + "] against base location: " + location);
				}
				Resource resource = location.createRelative(path);
				resourceResolver.getResource("/resources/domains/woego/admin/default/readme.txt").exists();
				if (resource.exists() && resource.isReadable()) {
					if (logger.isDebugEnabled()) {
						logger.debug("Found matching resource: " + resource);
					}
					if(this.useCache())
						locationCache.put(new LocationInfo(this.getDomain(request), this.getModule(request), theme, url),resource);
					return resource;
				}
				else if (logger.isTraceEnabled()) {
					logger.trace("Relative resource doesn't exist or isn't readable: " + resource);
				}
			}
			catch (IOException ex) {
				logger.debug("Failed to create relative resource - trying next resource location", ex);
			}
		}
		return null;
	}
	
	private void init() {
		
	}
	public ThemeResolver getResolver() {
		return resolver;
	}
	public void setResolver(ThemeResolver resolver) {
		this.resolver = resolver;
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		//super.afterPropertiesSet();
	}
	protected String getDomain(HttpServletRequest request) {
		return this.domainResolver.resolverDomainName(request);
	}
	protected String getModule(HttpServletRequest request) {
		return this.moduleResolver.resolverDomainName(request);
	}


	@Override
	protected void initServletContext(ServletContext servletContext) {
		super.initServletContext(servletContext);
		resourceResolver=new ServletContextResourcePatternResolver(servletContext);  
	}

	
	public class LocationInfo {
		private String domain;
		private String module;
		private String theme;
		private String url;
		private Resource resource;
		
		
		public LocationInfo(String domain, String module, String theme,
				String url) {
			super();
			this.domain = domain;
			this.module = module;
			this.theme = theme;
			this.url = url;
		}


		public LocationInfo(String domain, String module, String theme,
				String url,Resource resource) {
			super();
			this.domain = domain;
			this.module = module;
			this.theme = theme;
			this.url = url;
			this.resource=resource;
		}
		
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((domain == null) ? 0 : domain.hashCode());
			result = prime * result
					+ ((module == null) ? 0 : module.hashCode());
			result = prime * result
					+ ((resource == null) ? 0 : resource.hashCode());
			result = prime * result + ((theme == null) ? 0 : theme.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LocationInfo other = (LocationInfo) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (domain == null) {
				if (other.domain != null)
					return false;
			} else if (!domain.equals(other.domain))
				return false;
			if (module == null) {
				if (other.module != null)
					return false;
			} else if (!module.equals(other.module))
				return false;
			if (resource == null) {
				if (other.resource != null)
					return false;
			} else if (!resource.equals(other.resource))
				return false;
			if (theme == null) {
				if (other.theme != null)
					return false;
			} else if (!theme.equals(other.theme))
				return false;
			if(url == null){
				if(other.url != null){
					return false;
				}
			}else if(!url.equals(other.url)){
				return false;
			}
			return true;
		}
		private ButterflyThemeResourceHandler getOuterType() {
			return ButterflyThemeResourceHandler.this;
		}


		public String getUrl() {
			return url;
		}
		
		
	}

	public boolean useCache() {
		return "true".equals(Application.getValue(WebConstants.THEME_REOURCE_CACHE));
	}

}
