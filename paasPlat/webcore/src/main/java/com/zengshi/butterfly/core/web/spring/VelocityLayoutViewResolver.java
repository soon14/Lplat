/**
 * 
 */
package com.zengshi.butterfly.core.web.spring;

import java.util.List;

import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

import com.zengshi.butterfly.core.config.Application;
import com.zengshi.butterfly.core.velocity.ControllerRender;
import com.zengshi.butterfly.core.web.WebConstants;
import com.zengshi.butterfly.core.web.velocity.RenderInterceptor;

/**
 *
 */
public class VelocityLayoutViewResolver extends  VelocityViewResolver {

	private String layoutUrl;

	private String layoutKey;

	private String screenContentKey;
	
	private List<RenderInterceptor> renderInterceptors;
	
	
	private ControllerRender controllerRender;
	
	
	@Override
	protected Class requiredViewClass() {
		// TODO Auto-generated method stub
		return VelocityLayoutView.class;
	}


	public List<RenderInterceptor> getRenderInterceptors() {
		return renderInterceptors;
	}

	public void setRenderInterceptors(List<RenderInterceptor> renderInterceptors) {
		this.renderInterceptors = renderInterceptors;
	}
	
	
	/**
	 * Set the layout template to use. Default is "layout.vm".
	 * @param layoutUrl the template location (relative to the template
	 * root directory)
	 * @see VelocityLayoutView#setLayoutUrl
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
	 * <p>The default key is "layout", as illustrated above.
	 * @param layoutKey the name of the key you wish to use in your
	 * screen content templates to override the layout template
	 * @see VelocityLayoutView#setLayoutKey
	 */
	public void setLayoutKey(String layoutKey) {
		this.layoutKey = layoutKey;
	}

	/**
	 * Set the name of the context key that will hold the content of
	 * the screen within the layout template. This key must be present
	 * in the layout template for the current screen to be rendered.
	 * <p>Default is "screen_content": accessed in VTL as
	 * <code>$screen_content</code>.
	 * @param screenContentKey the name of the screen content key to use
	 * @see VelocityLayoutView#setScreenContentKey
	 */
	public void setScreenContentKey(String screenContentKey) {
		this.screenContentKey = screenContentKey;
	}


	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		VelocityLayoutView view = (VelocityLayoutView) super.buildView(viewName);
		// Use not-null checks to preserve VelocityLayoutView's defaults.
		if (this.layoutUrl != null) {
			view.setLayoutUrl(this.layoutUrl);
		}
		if (this.layoutKey != null) {
			view.setLayoutKey(this.layoutKey);
		}
		if (this.screenContentKey != null) {
			view.setScreenContentKey(this.screenContentKey);
		}
		
		view.setRenderInterceptors(this.renderInterceptors);
		
		view.setControl(this.controllerRender);
		return view;
	}


	/**
	 * @param controllerRender the controllerRender to set
	 */
	public void setControllerRender(ControllerRender controllerRender) {
		this.controllerRender = controllerRender;
	}


	@Override
	protected String getSuffix() {
		return Application.getValue(WebConstants.VIEW_VELOCITY_SUFFIX);
	}
	
}
