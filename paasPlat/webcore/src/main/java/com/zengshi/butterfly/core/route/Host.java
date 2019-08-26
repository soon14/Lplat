package com.zengshi.butterfly.core.route;
@Deprecated
public class Host {

	protected String ip;
	protected int port;
	
	protected int priority;

	private static final int DEFAULT_PORT=80;
	private static final int DEFAULT_PRIORITY=100;
	
	public Host(String ip) {
		this(ip,DEFAULT_PORT);
	}
	public Host(String ip, int port) {
		this(ip,port,DEFAULT_PRIORITY);
		
	}

	public Host(String ip, int port, int priority) {
		super();
		this.ip = ip;
		this.port = port;
		this.priority = priority;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}



	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	@Override
	public String toString() {
		return "Host [ip=" + ip + ", port=" + port + ", priority=" + priority
				+ "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + port;
		result = prime * result + priority;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Host other = (Host) obj;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (port != other.port)
			return false;
		if (priority != other.priority)
			return false;
		return true;
	}
}
