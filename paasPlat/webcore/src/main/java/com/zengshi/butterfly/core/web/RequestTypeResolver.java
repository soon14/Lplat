package com.zengshi.butterfly.core.web;

import javax.servlet.http.HttpServletRequest;

public interface RequestTypeResolver {

	public enum RequestType {
        JSON,XML,PAGE
    }
	public RequestType resolver(HttpServletRequest request);
}
