package com.zengshi.butterfly.core.route;

import org.springframework.core.Ordered;

/**
 * 主机路由
 *
 */
@Deprecated
public class Route implements Ordered {

	private String ip;
	
	private String port;
	
	private int order=MAX_PRIOTY;
	
	private static final int MAX_PRIOTY=100;
	
	private static final String DEFAULT_PORT="80";
	
	public Route(String ip ) {
		this(ip,DEFAULT_PORT,MAX_PRIOTY);
	}
	
	public Route(String ip, String port) {
		this(ip,port,MAX_PRIOTY);
	}
	

	public Route(String ip, String port, int order) {
		super();
		this.ip = ip;
		this.port = port;
		this.order = order;
	}



	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return this.order;
	}

	public String getIp() {
		return ip;
	}

	public String getPort() {
		return port;
	}

	@Override
	public String toString() {
		return "Route [ip=" + ip + ", port=" + port + ", order=" + order + "]";
	}
	
	
}
