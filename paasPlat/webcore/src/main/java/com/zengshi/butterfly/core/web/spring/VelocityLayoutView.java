package com.zengshi.butterfly.core.web.spring;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.view.context.ChainedContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.MethodInvocationException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedIOException;
import org.springframework.core.OrderComparator;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.view.velocity.VelocityConfig;
import org.springframework.web.servlet.view.velocity.VelocityToolboxView;

import com.zengshi.butterfly.core.velocity.ControllerRender;
import com.zengshi.butterfly.core.web.PageContextTool;
import com.zengshi.butterfly.core.web.URLBuilder;
import com.zengshi.butterfly.core.web.velocity.RenderInterceptor;


public class VelocityLayoutView extends VelocityToolboxView{

	private static String KEY_CONTROLLER_RENDER="__controller_render__";
	
	private static String KEY_CONTROL_TEMPLATE_CONTENT="__control_template_content__";
	
	private List<RenderInterceptor> renderInterceptors;
	
	
	
	/**
	 * The default {@link #setLayoutUrl(String) layout url}.
	 */
	public static final String DEFAULT_LAYOUT_URL = "layout.vm";

	/**
	 * The default {@link #setLayoutKey(String) layout key}.
	 */
	public static final String DEFAULT_LAYOUT_KEY = "layout";

	/**
	 * The default {@link #setScreenContentKey(String) screen content key}.
	 */
	public static final String DEFAULT_SCREEN_CONTENT_KEY = "screen_content";


	private String layoutUrl = DEFAULT_LAYOUT_URL;

	private String layoutKey = DEFAULT_LAYOUT_KEY;

	private String screenContentKey = DEFAULT_SCREEN_CONTENT_KEY;
	

	private ControllerRender control;


	/**
	 * Set the layout template to use. Default is {@link #DEFAULT_LAYOUT_URL "layout.vm"}.
	 * @param layoutUrl the template location (relative to the template
	 * root directory)
	 */
	public void setLayoutUrl(String layoutUrl) {
		this.layoutUrl = layoutUrl;
	}

	/**
	 * Set the context key used to specify an alternate layout to be used instead
	 * of the default layout. Screen content templates can override the layout
	 * template that they wish to be wrapped with by setting this value in the
	 * template, for example:<br>
	 * <code>#set( $layout = "MyLayout.vm" )</code>
	 * <p>Default key is {@link #DEFAULT_LAYOUT_KEY "layout"}, as illustrated above.
	 * @param layoutKey the name of the key you wish to use in your
	 * screen content templates to override the layout template
	 */
	public void setLayoutKey(String layoutKey) {
		this.layoutKey = layoutKey;
	}

	/**
	 * Set the name of the context key that will hold the content of
	 * the screen within the layout template. This key must be present
	 * in the layout template for the current screen to be rendered.
	 * <p>Default is {@link #DEFAULT_SCREEN_CONTENT_KEY "screen_content"}:
	 * accessed in VTL as <code>$screen_content</code>.
	 * @param screenContentKey the name of the screen content key to use
	 */
	public void setScreenContentKey(String screenContentKey) {
		this.screenContentKey = screenContentKey;
	}


	/**
	 * Overrides <code>VelocityView.checkTemplate()</code> to additionally check
	 * that both the layout template and the screen content template can be loaded.
	 * Note that during rendering of the screen content, the layout template
	 * can be changed which may invalidate any early checking done here.
	 */
	@Override
	public boolean checkResource(Locale locale) throws Exception {
		if (!super.checkResource(locale)) {
			return false;
		}

		try {
			// Check that we can get the template, even if we might subsequently get it again.
			//getTemplate(this.layoutUrl);
			return true;
		}
		catch (ResourceNotFoundException ex) {
			throw new NestedIOException("Cannot find Velocity template for URL [" + this.layoutUrl +
					"]: Did you specify the correct resource loader path?", ex);
		}
		catch (Exception ex) {
			throw new NestedIOException(
					"Could not load Velocity template for URL [" + this.layoutUrl + "]", ex);
		}
	}
	
	
	
	/*@Autowired
	private List<VelocityEngine> velocityEngines;*/
	@Override
	protected void doRender(Context context, HttpServletResponse response)
			throws Exception {
		HttpServletRequest request=((ChainedContext)context).getRequest();
		
		/*if(renderInterceptors != null) {
			for(RenderInterceptor interceptor:renderInterceptors) {
				interceptor.preRender(context, request,response);
			}
		}*/
		
		boolean isLayout=true;
		//TODO 不需要layout
		String uri=request.getRequestURI();

		Boolean isControllerRender=(Boolean)request.getAttribute(KEY_CONTROLLER_RENDER);
		
		if(isControllerRender != null && isControllerRender) {
			StringWriter  sw=new StringWriter();
			mergeTemplate(getTemplate(), context, sw);
			request.setAttribute(KEY_CONTROL_TEMPLATE_CONTENT, sw.toString());
			//context.put("", sw.toString());
			return ;
		}
		this.doRenderView(context, response);
	}
	protected void mergeTemplate(
			Template template, Context context, StringWriter sw) throws Exception {

		try {
			template.merge(context, sw);
		}
		catch (MethodInvocationException ex) {
			
		}
	}
	
	public List<RenderInterceptor> getRenderInterceptors() {
		return renderInterceptors;
	}
	public void setRenderInterceptors(List<RenderInterceptor> renderInterceptors) {
		this.renderInterceptors = renderInterceptors;
	}
	
	
	protected void doRenderView(Context context, HttpServletResponse response) throws Exception {
		renderScreenContent(context);

		// Velocity context now includes any mappings that were defined
		// (via #set) in screen content template.
		// The screen template can overrule the layout by doing
		// #set( $layout = "MyLayout.vm" )
		String layoutUrlToUse = findLayout(context,this.getUrl());
		
		if (layoutUrlToUse != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Screen content template has requested layout [" + layoutUrlToUse + "]");
			}
		}
		else {
			// No explicit layout URL given -> use default layout of this view.
			layoutUrlToUse = this.layoutUrl;
		}

		mergeTemplate(getTemplate(layoutUrlToUse), context, response);
	}
	
	private String findLayout(Context context,String tempalte) {
		String layout=DEFAULT_LAYOUT_URL;
		int speratorIdx=tempalte.lastIndexOf("/")==-1?0:tempalte.lastIndexOf("/");
		String suffix=tempalte.substring(0,speratorIdx);
		try {
			layout=suffix+"/layout.vm";
			getTemplate(layout);
			return layout;
		} catch (Exception e) {
			if(speratorIdx > 0) {
				return findLayout(context,suffix);
			}else {
				if("".equals(suffix)) {
					return "default_layout.vm";
				}else 
				return DEFAULT_LAYOUT_URL;
			}
		}
	}

	/**
	 * The resulting context contains any mappings from render, plus screen content.
	 */
	private void renderScreenContent(Context velocityContext) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Rendering screen content template [" + getUrl() + "]");
		}

		StringWriter sw = new StringWriter();
		Template screenContentTemplate = getTemplate(getUrl());
		screenContentTemplate.merge(velocityContext, sw);

		// Put rendered content into Velocity context.
		velocityContext.put(this.screenContentKey, sw.toString());
	}

	@Override
	protected void exposeHelpers(Context velocityContext,
			HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		super.exposeHelpers(velocityContext, request);
		
		// TODO Auto-generated method stub
		if(request.getAttribute("pageContext") == null) {
			PageContextTool pageContext=new PageContextTool();
			pageContext.setRequest(request);
			pageContext.setContextPath(request.getContextPath());
			velocityContext.put("pageContext", pageContext);
			request.setAttribute("pageContext", pageContext);
		}
		
		URLBuilder build=URLBuilder.getInstance("", request.getContextPath());
		
		velocityContext.put("URIBuilder", build);
		
		velocityContext.put("controlTempalte", control);
	}

	public ControllerRender getControl() {
		return control;
	}

	public void setControl(ControllerRender control) {
		this.control = control;
	}
	
	
	
	
	
	
	/*@Override
	protected VelocityEngine autodetectVelocityEngine() throws BeansException {
		// TODO Auto-generated method stub
		
		Map<String,VelocityConfig> velocityConfig = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				getApplicationContext(), VelocityConfig.class, true, false);
		
		
		if (!velocityConfig.isEmpty()) {
			List<VelocityEngine>  configs= new ArrayList<VelocityEngine>();
			for(VelocityConfig vc:velocityConfig.values()) {
				configs.add(vc.getVelocityEngine());
			}
			
			// We keep HandlerExceptionResolvers in sorted order.
			OrderComparator.sort(configs);
			this.velocityEngines=configs;
		}
		
		return null;
	}
	
	
	public List<VelocityEngine> getVelocityEngines() {
		return velocityEngines;
	}
	public void setVelocityEngines(List<VelocityEngine> velocityEngines) {
		this.velocityEngines = velocityEngines;
	}
	@Override
	protected Template getTemplate(String name) throws Exception {
		// TODO Auto-generated method stub
		Template template = null;
		for(VelocityEngine engine:this.velocityEngines) {
			template= (getEncoding() != null ?
					engine.getTemplate(name, getEncoding()) :
						engine.getTemplate(name));
			if(template != null) break;
		}
		
		
		return template;
	}*/
	

}
