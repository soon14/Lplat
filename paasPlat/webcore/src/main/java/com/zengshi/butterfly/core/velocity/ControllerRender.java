/**
 * 
 */
package com.zengshi.butterfly.core.velocity;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.OrderComparator;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.zengshi.butterfly.core.web.ButterflyHttpRequest;
import com.zengshi.butterfly.core.web.ButterflyWebConstants;
import com.zengshi.butterfly.core.web.ControllerClassNameHandlerMapping;
import com.zengshi.butterfly.core.web.InternalPathMethodNameResolver;
import com.zengshi.butterfly.core.web.RequestContext;
import com.zengshi.butterfly.core.web.spring.DefaultRequestToViewNameTranslator;

/**
 *
 */
public class ControllerRender implements Renderable, ApplicationContextAware,InitializingBean,ButterflyWebConstants {
	
	protected final Log logger = LogFactory.getLog(getClass());
	


	private LinkedList<ControlParameters> controlParameterStack = new LinkedList<ControllerRender.ControlParameters>();
	
	@Autowired
	@Qualifier(value="requestMappingHandlerMapping")
	private RequestMappingHandlerMapping handlerMapping;
	
	@Autowired
	@Qualifier(value="viewNameTranslator")
	private DefaultRequestToViewNameTranslator viewNameResolver;
	
	
	@Autowired
	private List<HandlerAdapter>  handlerAdapters;
	
	private ApplicationContext applicationContext;
	
	@Autowired
	private List<ViewResolver> viewResolvers;
	
	public static final String HANDLER_MAPPING_BEAN_NAME = "handlerMapping";
	
	
	//private List<HandlerMapping> handlerMappings;
	
	@Override
	public String render() {
		
		//HandlerMapping hm = applicationContext.getBean(HANDLER_MAPPING_BEAN_NAME, HandlerMapping.class);
		//this.handlerMappings = Collections.singletonList(hm);
		
		ModelAndView mv = null;
		
		HttpServletRequest request=RequestContext.getRequest();;
		HttpServletResponse response=RequestContext.getResponse();;
		
		try {
			/*for(String path:handlerMapping.getHandlerMap().keySet()) {
				if(handlerMapping.getPathMatcher().match(path,this.getControlParameters().template)) {
					request.setAttribute(KEY_CONTROLLER_TEMPLATE_URL, this.getControlParameters().template);
					Object handle=handlerMapping.getHandlerMap().get(path);

					HandlerAdapter ha = getHandlerAdapter(handle);
					HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
					mv = ha.handle(request, response, handle);
					
					request.removeAttribute(KEY_CONTROLLER_TEMPLATE_URL);
					
					applyDefaultViewName(this.getControlParameters().template, mv);
					
					if (mv != null && !mv.wasCleared()) {
						return render(mv, request, response);
					}
					break;
					
				}
			}*/
			HttpServletRequest processedRequest=new ButterflyHttpRequest(request, this.getControlParameters().template);
			HandlerExecutionChain mappedHandler  = getHandler(processedRequest);
			
			
			HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
			
			mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
			
			applyDefaultViewName(this.getControlParameters().template, mv);
			
			if (mv != null && !mv.wasCleared()) {
				return render(mv, request, response);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		return "";
	}
	
	protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		return handlerMapping.getHandler(request);
		/*for (HandlerMapping hm : this.handlerMappings) {
			HandlerExecutionChain handler = hm.getHandler(request);
			if (handler != null) {
				return handler;
			}
		}*/
		//return null;
	}

	
	protected String render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {

		View view;
		if (mv.isReference()) {
			// We need to resolve the view name.
			view = resolveViewName(mv.getViewName(), null, null, request);
			if (view == null) {
				throw new ServletException(
						"Could not resolve view with name '" + mv.getViewName() + "' in servlet with name '" +
								this.getClass() + "'");
			}
		}
		else {
			view = mv.getView();
			if (view == null) {
				throw new ServletException("ModelAndView [" + mv + "] neither contains a view name nor a " +
						"View object in servlet with name '" + this.getClass()+ "'");
			}
		}
		
		request.setAttribute(KEY_CONTROLLER_RENDER, Boolean.TRUE);
		
		Map<String,Object> model=mv.getModel();
		for(String key:this.getControlParameters().keySet()) {
			model.put(key, this.getControlParameters().get(key));
		}
		view.render(model, request, response);
		String content=(String)request.getAttribute(KEY_CONTROL_TEMPLATE_CONTENT);
		request.removeAttribute(KEY_CONTROLLER_RENDER);
		request.removeAttribute(KEY_CONTROL_TEMPLATE_CONTENT);
		return content;
	}
	
	//TODO 这里以后可以处理成按用户local来确定显示的界面语言
	protected View resolveViewName(String viewName, Map<String, Object> model, Locale locale,
			HttpServletRequest request) throws Exception {

		for (ViewResolver viewResolver : this.viewResolvers) {
			View view = viewResolver.resolveViewName(viewName, locale);
			if (view != null) {
				return view;
			}
		}
		return null;
	}
	
	private void applyDefaultViewName(String requestURI, ModelAndView mv) throws Exception {
		if (mv != null && !mv.hasView()) {
			mv.setViewName(getDefaultViewName(requestURI));
		}
	}
	
	protected String getDefaultViewName(String reuquestURI) throws Exception {
		
		return this.viewNameResolver.getViewName(reuquestURI);
	}
	
	 /**
     * 设置control的模板。此方法和<code>setModule</code>只能执行其一，否则将忽略后者。
     *
     * @param template control模板名
     * @return <code>ControlTool</code>本身，以方便模板中的操作
     */
    public ControllerRender setTemplate(String template) {
        ControlParameters params = getControlParameters();

        if (params.module == null) {
            params.template = template+".html";
        }

        return this;
    }

    /** 取得栈顶的control参数。 */
    protected ControlParameters getControlParameters() {
        if (controlParameterStack.isEmpty()) {
            controlParameterStack.addFirst(new ControlParameters());
        }

        return controlParameterStack.getFirst();
    }
    
    /**
     * 设置control的参数。
     * <p>
     * 这些参数将被保存在一个一次性的<code>Map</code>中，当render成功以后，该map就被丢弃，以便再次调用该control。
     * </p>
     *
     * @param name  属性名
     * @param value 对象
     * @return <code>ControlTool</code>本身，以方便模板中的操作
     */
    public ControllerRender setParameter(String name, Object value) {
        ControlParameters params = getControlParameters();

        params.put(name, value);

        return this;
    }
    
    protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
		for (HandlerAdapter ha : this.handlerAdapters) {
			
			if (ha.supports(handler)) {
				return ha;
			}
		}
		return null;
	}
    
    
	/** 代表一次control的调用参数。 */
    protected static class ControlParameters extends HashMap<String, Object> {
        private static final long serialVersionUID = 3256721796996084529L;
        private String      module;
        private String      template;
        private Set<String> exportVars;

        public ControlParameters() {
            super(4);
        }
    }



	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext=applicationContext;
		
	}

	public void setViewNameResolver(
			DefaultRequestToViewNameTranslator viewNameResolver) {
		this.viewNameResolver = viewNameResolver;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		OrderComparator.sort(this.viewResolvers);
		
		//HandlerMapping hm = applicationContext.getBean(HANDLER_MAPPING_BEAN_NAME, HandlerMapping.class);
		//this.handlerMappings = Collections.singletonList(hm);
		
	}



}
