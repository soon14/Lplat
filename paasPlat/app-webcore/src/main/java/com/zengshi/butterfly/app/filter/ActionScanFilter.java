/**
 * 
 */
package com.zengshi.butterfly.app.filter;

import com.zengshi.butterfly.app.ActionConfig;
import com.zengshi.butterfly.app.ApplicationConfig;
import com.zengshi.butterfly.app.annotation.Action;
import com.zengshi.butterfly.app.annotation.Request;
import com.zengshi.butterfly.app.annotation.Response;
import com.zengshi.butterfly.core.annotation.Config;
import com.zengshi.butterfly.core.config.Application;
import com.zengshi.butterfly.core.reflect.ClassUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@Component
@Lazy(value=false)
public class ActionScanFilter implements IFilter<Object> {
	
	@Config(value="action.package")
	private String actionPackage;
	
	@PostConstruct 
	public void init() 
	{	
		System.out.println("======================ActionScanFilter actionPackage:"+actionPackage+"========================"+Application.getValue("action.package"));
		Set<Class<?>> types = new HashSet<Class<?>>(0);
		String[] packages = this.getActionPackage().split(",");
		
		for(String p : packages)
			types.addAll(ClassUtils.getClasses(p));
		
    	for(Class type:types) {
    		
    		Action action=AnnotationUtils.findAnnotation(type, com.zengshi.butterfly.app.annotation.Action.class);
    		if(action != null) {
    			String bizcode=AnnotationUtils.getValue(action, "bizcode").toString();
    			Boolean userCheck=(Boolean)AnnotationUtils.getValue(action, "userCheck");
    			Boolean rightCheck=(Boolean)AnnotationUtils.getValue(action, "rightCheck");
    			Boolean ipCheck=(Boolean)AnnotationUtils.getValue(action, "ipCheck");
    			Field[] fields=type.getDeclaredFields();
    			Class requestType=null;
    			Class responseType= null;
    			for(Field field:fields) {
    				Request request=field.getAnnotation(Request.class);
    				if(request != null) {
    					requestType=field.getType();
    				}
    				
    				Response response=field.getAnnotation(Response.class);
    				if(response != null) {
    					responseType=field.getType();
    				}
    			}
    			ActionConfig config=new ActionConfig(bizcode, type, requestType, responseType);
    			config.setUserCheck(userCheck);
    			config.setIpCheck(ipCheck);
    			config.setRightCheck(rightCheck);
    			ApplicationConfig.addActionConfig(bizcode, config);
    		}
    	}
	}

	/* (non-Javadoc)
	 * @see com.zengshi.uni.core.IFilter#destroy()
	 */
	public void destroy() {
		return;
	}

	public String getActionPackage() {
		return actionPackage;
	}

	public void setActionPackage(String actionPackage) {
		this.actionPackage = actionPackage;
	}

	@Override
	public Object doFilter(Object context) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
