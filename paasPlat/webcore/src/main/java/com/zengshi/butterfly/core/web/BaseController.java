/**
 * 
 */
package com.zengshi.butterfly.core.web;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 *
 */
public class BaseController  /*extends MultiActionController*/  {
	
//	/**
//	 * 当前用户是否代理商
//	 * @return
//	 */
//	public boolean currentUserIsAnAgent(){
//		return  false;
//	}
	

	/*@Autowired
	public void setInternalMethodNameResolver(
			MethodNameResolver internalMethodNameResolver) {
		super.setMethodNameResolver(internalMethodNameResolver);
	}*/

	
	public String getParam(HttpServletRequest request,String name) {
		
		String method=request.getMethod();
		try {
			if("PUT".equals(method)) {
				return new String(request.getParameter(name).getBytes("iso8859-1"),"UTF-8");
			}else {
				return request.getParameter(name);
			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	 

	public class JsonObject {
		private int total;
		private boolean success;
		private String message;
		
		private Object data;
		
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		
		
		public JsonObject(List data) {
			this(true,data);
		}
		public JsonObject(boolean success,List data) {
			if(data != null) {
				this.total=data.size();
				this.data=data;
				this.success=success;
			}
		}
		
		public JsonObject(boolean success,String msg) {
			this.success =success;
			this.message=msg;
		}
		public JsonObject(boolean success,String msg,Object data) {
			this.success =success;
			this.message=msg;
			this.data=data;
		}
		
		public int getTotal() {
			return total;
		}
		public void setTotal(int total) {
			this.total = total;
		}
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public Object getData() {
			return data;
		}
		public void setData(Object data) {
			this.data = data;
		}
		
		public String toJsonString() {
			ObjectMapper om=new ObjectMapper();
			try {
				return om.writeValueAsString(this);
			} catch (Exception e) {
				// TODO: handle exception
			}
			return null;
		}

	}
	
	public class KeyValueSet extends HashMap {

		public KeyValueSet() {
			super();
			// TODO Auto-generated constructor stub
		}

		public KeyValueSet(String keyproperty,String key,String valueproperty,String value) {
			super();
			// TODO Auto-generated constructor stub
			this.put(keyproperty, key);
			this.put(valueproperty, value);
		}
		public KeyValueSet(String key,String value) {
			this("key",key,"value",value);
		}
		
	}

/*	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		ModelAndView mv;
		try {
			String methodName = this.getMethodNameResolver().getHandlerMethodName(request);
			mv= invokeNamedMethod(methodName, request, response);
		}
		catch (NoSuchRequestHandlingMethodException ex) {
			try {
				String methodName ="viewPage";
				if(METHOD_GET.equals(request.getMethod())) {
					methodName ="viewPage";
				}else if(METHOD_POST.equals(request.getMethod())) {
					methodName ="doSubmit";
				}
				
				mv= invokeNamedMethod(methodName, request, response);
			} catch (NoSuchRequestHandlingMethodException e) {
				return handleNoSuchRequestHandlingMethod(ex, request, response);
			}
		}
		//TODO 添加请求地址对ModelAndView的限制
		String uri=request.getRequestURI();
		String suffix=uri.substring(uri.lastIndexOf(".")+1);
		if("html".equals(suffix)) {
			
		}else if("json".equals(suffix)) {
			if(!JsonModelView.class.isAssignableFrom(mv.getClass())) {
				throw new RuntimeException("返回类型不正确");
			}
		}else if("xml".equals(suffix))  {
			
		}
		return mv;
	}
	

	public ModelAndView viewPage(HttpServletRequest request, HttpServletResponse respnose) {
		return new ModelAndView();
	}
	
	public ModelAndView doSubmit(HttpServletRequest request, HttpServletResponse respnose) {
		return new ModelAndView();
	}*/
	
}
