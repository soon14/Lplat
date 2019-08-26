/**
 * 
 */
package com.zengshi.butterfly.core.web.velocity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;
import org.springframework.beans.factory.annotation.Autowired;

import com.zengshi.butterfly.core.velocity.ControllerRender;
import com.zengshi.butterfly.core.web.PageContextTool;
import com.zengshi.butterfly.core.web.URLBuilder;

/**
 *删除，该逻辑由com.zengshi.butterfly.core.web.spring.VelocityLayoutView.exposeHelpers(Context velocityContext, HttpServletRequest request) throws Exception 实现
 */
@Deprecated
public class DefaultRenderInterceptor implements RenderInterceptor {

	@Autowired
	private ControllerRender control;
	
	@Override
	public void preRender(Context context, HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		if(request.getAttribute("pageContext") == null) {
			PageContextTool pageContext=new PageContextTool();
			pageContext.setRequest(request);
			pageContext.setContextPath(request.getContextPath());
			context.put("pageContext", pageContext);
			request.setAttribute("pageContext", pageContext);
		}
		
		URLBuilder build=URLBuilder.getInstance("", request.getContextPath());
		
		context.put("URIBuilder", build);
		
		context.put("controlTempalte", control);
	}

	public ControllerRender getControl() {
		return control;
	}

	public void setControl(ControllerRender control) {
		this.control = control;
	}



}
