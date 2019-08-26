package com.zengshi.butterfly.core.afs;

import java.net.MalformedURLException;
import java.net.URL;

public class FTPConnectInfo implements IAFSConnection {

	private static final String protocol="ftp";
	private String ip;
	private int port;
	private String username;
	private String password;
	
	public FTPConnectInfo(String ip, String username, String password) {
		this(ip,21,username,password);
	}

	public FTPConnectInfo(String ip, int port, String username,
			String password) {
		super();
		this.ip = ip;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	
	@Override
	public String getConnectionString() {
		return new StringBuffer(protocol).append("://")
				.append(username).append(":")
				.append(password).append("@")
				.append(ip).append(":")
				.append(port).toString();
	}
	
	public URL getResourceURL(String filePath) {
		try {
			return new URL(this.getConnectionString()+""+filePath);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
