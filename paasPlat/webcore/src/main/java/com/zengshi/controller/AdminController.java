/**
 * 
 */
package com.zengshi.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zengshi.butterfly.core.annotation.Security;
import com.zengshi.butterfly.core.web.spring.ExtendedPropertyPlaceholderConfigurer;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 */
@Controller
public class AdminController {

	@Autowired
	private ExtendedPropertyPlaceholderConfigurer properties;
	
	@RequestMapping(value="/visitor/application",method=RequestMethod.GET)
	@Security(mustLogin=true,userName="admin",comment="查看当前服务器所使用的有效配置信息")
	public ModelAndView configShow(HttpServletRequest request, HttpServletResponse respnose,HttpSession session) throws Exception{
		ModelAndView mav=new ModelAndView();
		String visitorResult=properties.visitor();

		ObjectMapper om=new ObjectMapper();
		Map<String, String> m=om.readValue(visitorResult,HashMap.class);
		mav.getModelMap().put("values",new TreeMap(m));

		return mav;
		
		
	}

	@RequestMapping(value="/visitor/application/reload",method=RequestMethod.GET)
	@Security(mustLogin=true,userName="admin",comment="查看当前服务器所使用的有效配置信息")
	public ModelAndView configReload(HttpServletRequest request, HttpServletResponse respnose,HttpSession session) throws Exception{
		ModelAndView mav=new ModelAndView("redirect:/visitor/application.html");
		
		properties.refresh();
			
		return mav;
		
		
	}
	
	public ExtendedPropertyPlaceholderConfigurer getProperties() {
		return properties;
	}

	public void setProperties(ExtendedPropertyPlaceholderConfigurer properties) {
		this.properties = properties;
	}
	
	
	
}
