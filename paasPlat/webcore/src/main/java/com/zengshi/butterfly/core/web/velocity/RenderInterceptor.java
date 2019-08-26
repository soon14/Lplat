package com.zengshi.butterfly.core.web.velocity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;

public interface RenderInterceptor {

	public void preRender(Context context, HttpServletRequest request, HttpServletResponse response)  ;
}
