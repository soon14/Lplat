package com.zengshi.butterfly.core.security;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.OrderComparator;

import com.zengshi.butterfly.core.annotation.Security;
import com.zengshi.butterfly.core.exception.BusinessException;
import com.zengshi.butterfly.core.web.security.AuthorException;
import com.zengshi.butterfly.core.web.security.AuthorizationResult;
import com.zengshi.butterfly.core.web.security.AuthorizationResult.ResultEnum;

@Aspect
public class PermissionAspect implements InitializingBean,ApplicationContextAware  {

	private ApplicationContext context;

	private List<SecurityCheckHandler> checkHandlers;
	
	@Before(value = "com.zengshi.butterfly.core.security.SystemArchitecture.userAccess()&&"
			+ "@annotation(security)", argNames = "security")
	public void checkPermission(Security security)
			throws Exception {
		
		//如果定义了类型则先处理类型
		if(security.authorCheckType() != null && !security.authorCheckType().isInterface()  && PermissionCheckHandler.class.isAssignableFrom(security.authorCheckType())) {
			PermissionCheckHandler handler=context.getBean(security.authorCheckType()) ;
			handler.isPermission(security);
			return;
		}
		if(checkHandlers != null ) {
			for(PermissionCheckHandler handler:this.checkHandlers) {
				if(!handler.isPermission(security)) {
					AuthorizationResult result=new AuthorizationResult();
					result.setResult(ResultEnum.NOT_PERMIT);
					result.setMessage("访问未授权");
					throw new AuthorException(result);
				}
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
		
		if(this.checkHandlers == null || checkHandlers.size() == 0) {
			
			Map<String,SecurityCheckHandler> handlers=context.getBeansOfType(SecurityCheckHandler.class, true, true);
			if(handlers != null && !handlers.isEmpty()) {
				this.checkHandlers=new ArrayList<SecurityCheckHandler>();
				for(SecurityCheckHandler handle:handlers.values()) {
					this.checkHandlers.add(handle);
				}	
			}
		}
		if(this.checkHandlers != null && this.checkHandlers.size() >0) {
			OrderComparator.sort(this.checkHandlers);
		}
		
		
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context=applicationContext;
	}

	public List<SecurityCheckHandler> getCheckHandlers() {
		return checkHandlers;
	}

	public void setCheckHandlers(List<SecurityCheckHandler> checkHandlers) {
		this.checkHandlers = checkHandlers;
	}

	

}
