/**
 * 
 */
package com.zengshi.butterfly.core.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 */
public class DispatcherServlet extends
		org.springframework.web.servlet.DispatcherServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1824150500946318114L;
	public static String[] IGNORE_SUFFIX = {};

	@Override
	protected void doDispatch(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (!shouldFilter(request)) {
			super.doDispatch(request, response);
			return;
		}
		HttpServletRequest _request = new ButterflyHttpRequest(request);
		RequestContext.setRequest(_request);
		RequestContext.setResponse(response);
		RequestContext.setHttpSession(_request.getSession());
		super.doDispatch(request, response);

	}

	@Override
	protected void doService(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			super.doService(request, response);
		} catch (Exception e) {
			throw e;
		} finally {
			RequestContext.clear();
		}

	}

	@Override
	protected void initStrategies(ApplicationContext context) {
		super.initStrategies(context);

	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String ignore_suffix = config.getInitParameter("ignore_suffix");
		if (null != ignore_suffix && !"".equals(ignore_suffix))
			IGNORE_SUFFIX = config.getInitParameter("ignore_suffix").split(",");
	}

	private boolean shouldFilter(HttpServletRequest request) {
		String uri = request.getRequestURI().toLowerCase();
		for (String suffix : IGNORE_SUFFIX) {
			if (uri.endsWith(suffix))
				return false;
		}
		return true;
	}

	@Override
	protected void render(ModelAndView mv, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		super.render(mv, request, response);
	}

}
