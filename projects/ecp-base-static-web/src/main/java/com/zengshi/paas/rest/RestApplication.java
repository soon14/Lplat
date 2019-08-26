package com.zengshi.paas.rest;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;

public class RestApplication extends ResourceConfig {

    public RestApplication() {
     //服务类所在的包路径
     packages("com.zengshi.paas.service");
      //打印访问日志，便于跟踪调试，正式发布可清除
     register(LoggingFilter.class);
    }

}
