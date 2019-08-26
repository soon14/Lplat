package com.zengshi.butterfly.core.web.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import com.zengshi.butterfly.core.config.Application;
import com.zengshi.butterfly.core.exception.BaseException;
import com.zengshi.butterfly.core.exception.BusinessException;
import com.zengshi.butterfly.core.exception.SystemException;
import com.zengshi.butterfly.core.web.WebConstants;
import com.zengshi.butterfly.core.web.RequestTypeResolver.RequestType;
import com.zengshi.butterfly.core.web.helper.WebHelper;
import com.zengshi.butterfly.core.web.security.AuthorException;

public class DefaultHandlerExceptionResolver extends WebApplicationObjectSupport   implements
		HandlerExceptionResolver ,InitializingBean{
	
	private static final Log logger = LogFactory.getLog(DefaultHandlerExceptionResolver.class);
	
	private static final String DEFAULT_VIEW="/common/error";
	
	private String viewName=DEFAULT_VIEW;
	
	private Map<String,ExceptionViewResolver> viewResolvers;
	
	
	private ContentNegotiationManager contentNegotiationManager;
	
	private ContentNegotiationManagerFactoryBean cnManagerFactoryBean = new ContentNegotiationManagerFactoryBean();
	

	@Override
	protected void initServletContext(ServletContext servletContext) {
		this.cnManagerFactoryBean.setServletContext(servletContext);
	}
	public void afterPropertiesSet() throws Exception {
		if (this.contentNegotiationManager == null) {
			this.cnManagerFactoryBean.afterPropertiesSet();
			this.contentNegotiationManager = this.cnManagerFactoryBean.getObject();
		}
	}
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		// TODO Auto-generated method stub
		//RequestType requestType=RequestType.PAGE;
		try {
			//处理自定义错误
			if(viewResolvers != null && viewResolvers.containsKey(ex.getClass().getCanonicalName())) {
				return viewResolvers.get(ex.getClass()).resolver(request, response, handler, ex);
						
			}
			
			List<MediaType> requestedMediaTypes = getMediaTypes(request);
			MediaType mediaType=null;
			if(requestedMediaTypes != null && requestedMediaTypes.size() >0) {
				mediaType=requestedMediaTypes.get(0);
			}
			
			//List<MediaType> meidaType=this.getMediaTypes(request);
			//处理系统默认错误
			if(ex instanceof AuthorException) {
				return handlerAuthorException(mediaType,request,response,handler,(AuthorException)ex);
			}else if(ex instanceof BusinessException) {
				return handlerBusinessException(mediaType,request,response,handler,(BusinessException)ex);
			}else if(ex instanceof SystemException) {
				return handlerSystemException(mediaType,request,response,handler,(SystemException)ex);
			}else {
				return handlerException(mediaType,request,response,handler,ex);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	private static class StringPrintWriter extends PrintWriter{
		 public StringPrintWriter(){  
		        super(new StringWriter());  
		    }  
		     
		    public StringPrintWriter(int initialSize) {  
		          super(new StringWriter(initialSize));  
		    }  
		     
		    public String getString() {  
		          flush();  
		          return ((StringWriter) this.out).toString();  
		    }  
		     
		    @Override  
		    public String toString() {  
		        return getString();  
		    } 
	}
	
	/**
	 * 处理认证信息
	 * @param requestType
	 * @param exception
	 * @return
	 */
	protected ModelAndView handlerAuthorException(MediaType mediaType,HttpServletRequest request,
			HttpServletResponse response, Object handler,AuthorException exception) {
	
		Map<String, Object> exceptionInfo =this.getExceptionInfo(mediaType, request, response, handler, exception);
		
		ModelAndView mav=this.createView(mediaType);
		// OK, NOT_LOGIN, NOT_PERMIT, EXPIRED;
		if(!MediaType.APPLICATION_JSON.equals(mediaType) && 
				!MediaType.APPLICATION_ATOM_XML.equals(mediaType) && 
				!MediaType.APPLICATION_XML.equals(mediaType)) {
			String redirect="redirect:";
			switch (exception.getResult().getResult()) {
			case OK:
				break;
			case NOT_LOGIN:
				String redirectURL=request.getRequestURL()+(request.getQueryString()==null?"":request.getQueryString());
				try {
					mav.setViewName(redirect+Application.getValue(WebConstants.URL_NOT_LOGIN)+"?redirect="+URLEncoder.encode(redirectURL,"UTF-8"))	;
					break;
				} catch (Exception e) {
					mav.setViewName(redirect+Application.getValue(WebConstants.URL_NOT_LOGIN));
				}
			case NOT_PERMIT:
				mav.setViewName(redirect+Application.getValue(WebConstants.URL_NOT_PERMIT));
				break;
			case EXPIRED:
				mav.setViewName(redirect+Application.getValue(WebConstants.URL_SESSION_EXPRIED));
				break;
			default:
				break;
			}
		}

		boolean isDebug=WebHelper.isDebugMode();
		if(isDebug) {
			mav.getModelMap().put("isDebug", isDebug);
		}
		
		mav.getModelMap().put("resp", exceptionInfo);
		
		return mav;
	}
	
	protected ModelAndView createView(MediaType mediaType) {
		/*switch (requestType) {
		case JSON:
			return new JsonModelView();
		case XML:
			return null;
		case PAGE:
			return new ModelAndView(this.viewName);
		default:
			return null;
		}*/
		
		return new ModelAndView(this.viewName);
	}
	
	protected ModelAndView handlerBusinessException(MediaType mediaType,HttpServletRequest request,
			HttpServletResponse response, Object handler,BusinessException exception) {

		Map<String, Object> exceptionInfo =this.getExceptionInfo(mediaType, request, response, handler, exception);
		boolean isDebug=WebHelper.isDebugMode();
		ModelAndView mav=this.createView(mediaType);
		mav.getModelMap().put("resp", exceptionInfo);
		if(isDebug) {
			mav.getModelMap().put("isDebug", isDebug);
		}
		

		return mav;
	}
	protected ModelAndView handlerSystemException(MediaType mediaType,HttpServletRequest request,
			HttpServletResponse response, Object handler,SystemException exception) {
		Map<String, Object> exceptionInfo =this.getExceptionInfo(mediaType, request, response, handler, exception);
		boolean isDebug=WebHelper.isDebugMode();
		ModelAndView mav=this.createView(mediaType);
		mav.getModelMap().put("resp", exceptionInfo);
		if(isDebug) {
			mav.getModelMap().put("isDebug", isDebug);
		}
		
		return mav;
	}
	protected ModelAndView handlerException(MediaType mediaType,HttpServletRequest request,
			HttpServletResponse response, Object handler,Exception exception) {
		Map<String, Object> exceptionInfo=new HashMap<String, Object>();
		exceptionInfo.put("code", "unknow_exception");
		exceptionInfo.put("msg", "服务器错误，请联系管理员");
		
		ModelAndView mav=this.createView(mediaType);
		
		boolean isDebug=WebHelper.isDebugMode();
		if(isDebug) {
			StringPrintWriter strintPrintWriter = new StringPrintWriter();  
			exception.printStackTrace(strintPrintWriter);  
			exceptionInfo.put("errorStack", strintPrintWriter.getString());
			mav.getModelMap().put("isDebug", isDebug);
		}
		mav.getModelMap().put("resp", exceptionInfo);
		
		
		return mav;
	}

	protected Map<String,Object> getExceptionInfo(MediaType mediaType,HttpServletRequest request,
			HttpServletResponse response, Object handler,BaseException exception) {
		Map<String, Object> exceptionInfo=new HashMap<String, Object>();
		exceptionInfo.put("code", exception.getErrorCode());
		exceptionInfo.put("msg", exception.getErrorMsg());

		boolean isDebug=WebHelper.isDebugMode();
		if(isDebug) {
			StringPrintWriter strintPrintWriter = new StringPrintWriter();  
			exception.printStackTrace(strintPrintWriter);  
			exceptionInfo.put("errorStack", strintPrintWriter.getString());
			
		}
		
		return exceptionInfo;
	}

	public String getViewName() {
		return viewName;
	}


	public void setViewName(String viewName) {
		this.viewName = viewName;
	}


	public Map<String, ExceptionViewResolver> getViewResolvers() {
		return viewResolvers;
	}

	public void setViewResolvers(Map<String, ExceptionViewResolver> viewResolvers) {
		this.viewResolvers = viewResolvers;
	}
	
	
	protected List<MediaType> getMediaTypes(HttpServletRequest request) {
		try {
			ServletWebRequest webRequest = new ServletWebRequest(request);
			List<MediaType> acceptableMediaTypes = this.contentNegotiationManager.resolveMediaTypes(webRequest);
			List<MediaType> producibleMediaTypes = getProducibleMediaTypes(request);
			Set<MediaType> compatibleMediaTypes = new LinkedHashSet<MediaType>();
			for (MediaType acceptable : acceptableMediaTypes) {
				for (MediaType producible : producibleMediaTypes) {
					if (acceptable.isCompatibleWith(producible)) {
						compatibleMediaTypes.add(getMostSpecificMediaType(acceptable, producible));
					}
				}
			}
			List<MediaType> selectedMediaTypes = new ArrayList<MediaType>(compatibleMediaTypes);
			MediaType.sortBySpecificityAndQuality(selectedMediaTypes);
			if (logger.isDebugEnabled()) {
				logger.debug("Requested media types are " + selectedMediaTypes + " based on Accept header types " +
						"and producible media types " + producibleMediaTypes + ")");
			}
			return selectedMediaTypes;
		}
		catch (HttpMediaTypeNotAcceptableException ex) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private List<MediaType> getProducibleMediaTypes(HttpServletRequest request) {
		Set<MediaType> mediaTypes = (Set<MediaType>)
				request.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
		if (!CollectionUtils.isEmpty(mediaTypes)) {
			return new ArrayList<MediaType>(mediaTypes);
		}
		else {
			return Collections.singletonList(MediaType.ALL);
		}
	}

	/**
	 * Return the more specific of the acceptable and the producible media types
	 * with the q-value of the former.
	 */
	private MediaType getMostSpecificMediaType(MediaType acceptType, MediaType produceType) {
		produceType = produceType.copyQualityValue(acceptType);
		return MediaType.SPECIFICITY_COMPARATOR.compare(acceptType, produceType) < 0 ? acceptType : produceType;
	}

	/**
	 * @return the contentNegotiationManager
	 */
	public ContentNegotiationManager getContentNegotiationManager() {
		return contentNegotiationManager;
	}

	/**
	 * @param contentNegotiationManager the contentNegotiationManager to set
	 */
	public void setContentNegotiationManager(
			ContentNegotiationManager contentNegotiationManager) {
		this.contentNegotiationManager = contentNegotiationManager;
	}


}
