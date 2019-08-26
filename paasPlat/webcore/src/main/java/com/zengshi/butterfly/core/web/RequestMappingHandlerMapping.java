package com.zengshi.butterfly.core.web;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.Set;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import com.zengshi.butterfly.core.annotation.Security;
import com.zengshi.butterfly.core.config.Application;

public class RequestMappingHandlerMapping
		extends
		org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping {

	private FileWriter fw;
	@Override
	protected void registerHandlerMethod(Object handler, Method method,
			RequestMappingInfo mapping) {
		// TODO Auto-generated method stub
		super.registerHandlerMethod(handler, method, mapping);
		
		Security security=method.getAnnotation(Security.class);
		try {
			StringBuffer line=new StringBuffer();
			line.append(mapping.toString());
			
			if(security != null) {
				line.append(security.comment());
			}
			
			fw.append(line).append("\r\n");
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			
		}
		
	}
	@Override
	protected void initHandlerMethods() {
		// TODO Auto-generated method stub
		try {
			fw=new FileWriter(Application.getValue("file.log.export-url-list"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.initHandlerMethods();
		try {
			fw.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}


}
