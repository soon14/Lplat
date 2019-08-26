package com.zengshi.butterfly.core.web.spring;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Locale;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.zengshi.butterfly.core.web.ButterflyHttpRequest;
import com.zengshi.butterfly.core.web.ButterflyHttpSession;

public class ButterflyServletRequestMethodArgumentResolver   implements HandlerMethodArgumentResolver{

	
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> paramType = parameter.getParameterType();
		return WebRequest.class.isAssignableFrom(paramType) ||
				ServletRequest.class.isAssignableFrom(paramType) ||
				MultipartRequest.class.isAssignableFrom(paramType) ||
				HttpSession.class.isAssignableFrom(paramType) ||
				Principal.class.isAssignableFrom(paramType) ||
				Locale.class.equals(paramType) ||
				InputStream.class.isAssignableFrom(paramType) ||
				Reader.class.isAssignableFrom(paramType);
	}

	public Object resolveArgument(
			MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
			throws IOException {
		
		Class<?> paramType = parameter.getParameterType();
		if (WebRequest.class.isAssignableFrom(paramType)) {
			return webRequest;
		}
		
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		if (ServletRequest.class.isAssignableFrom(paramType) || MultipartRequest.class.isAssignableFrom(paramType)) {
			Object nativeRequest = webRequest.getNativeRequest(paramType);
			if (nativeRequest == null) {
				throw new IllegalStateException(
						"Current request is not of type [" + paramType.getName() + "]: " + request);
			}
			return new ButterflyHttpRequest(request);
		}
		else if (HttpSession.class.isAssignableFrom(paramType)) {
			return new ButterflyHttpSession(request.getSession());
		}
		else if (Principal.class.isAssignableFrom(paramType)) {
			return request.getUserPrincipal();
		}
		else if (Locale.class.equals(paramType)) {
			return RequestContextUtils.getLocale(request);
		}
		else if (InputStream.class.isAssignableFrom(paramType)) {
			return request.getInputStream();
		}
		else if (Reader.class.isAssignableFrom(paramType)) {
			return request.getReader();
		}
		else {
			// should never happen..
			Method method = parameter.getMethod();
			throw new UnsupportedOperationException("Unknown parameter type: " + paramType + " in method: " + method);
		}
	}
	
}
