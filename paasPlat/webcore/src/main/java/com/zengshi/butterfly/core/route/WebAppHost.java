package com.zengshi.butterfly.core.route;

@Deprecated
public class WebAppHost extends Host {

	private String contextPath;
	public WebAppHost(String ip,int port,int priority ,String contextPath) {
		super(ip,port,priority);
		this.contextPath=contextPath;
	}
	public String getContextPath() {
		return contextPath;
	}
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((contextPath == null) ? 0 : contextPath.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		WebAppHost other = (WebAppHost) obj;
		if (contextPath == null) {
			if (other.contextPath != null)
				return false;
		} else if (!contextPath.equals(other.contextPath))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "WebAppHost [contextPath=" + contextPath + ", ip=" + ip
				+ ", port=" + port + ", priority=" + priority + "]";
	}

}
