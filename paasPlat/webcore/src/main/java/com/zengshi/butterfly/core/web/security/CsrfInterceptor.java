package com.zengshi.butterfly.core.web.security;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class CsrfInterceptor extends HandlerInterceptorAdapter{

	private List<String> postCommand;
	
	private Set<String> domains;
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String requestURI=this.getCommand(request);
		String method=request.getMethod();
		this.initDefault();
		//限制部分请求的command必须为post类型
		if(this.postCommand != null) {
			for(String command:postCommand) {
				/*if(requestURI.matches(command) && !"POST".equalsIgnoreCase(method)) {
					throw new RuntimeException("access not permit");
				}*/
			}
		}
		
		//TODO check refer
		/*String refererURI =new URI(request.getHeader("referer")).getPath();
		if(this.domains != null && this.domains.contains(refererURI)) {
			throw new RuntimeException("access not permit");
		}
		*/
		return super.preHandle(request, response, handler);
	}
	
	private void initDefault() {
		if(this.postCommand == null || this.postCommand.size() == 0) {
			this.postCommand =new ArrayList<String>();
			this.addCommandKey("add");
			this.addCommandKey("insert");
			this.addCommandKey("save");
			this.addCommandKey("update");
			this.addCommandKey("delete");
			this.addCommandKey("remove");
			
		}
	}
	
	private void addCommandKey(String postCommandKey) {
		this.postCommand.add("\\w*"+postCommandKey+"\\w*");
	}
	
	protected String getCommand(HttpServletRequest request) {
		String requestURI=request.getRequestURI();
		int pos1=requestURI.lastIndexOf("/");
		
		int pos2=requestURI.lastIndexOf(".");
		
		String command=requestURI.substring(pos1+1, pos2);
		
		return command;
		
	}
	
	public static void main(String[] arg) {
		
		String requestURI="/sys/user/update.html";
		
		int pos1=requestURI.lastIndexOf("/");
		
		int pos2=requestURI.lastIndexOf(".");
		
		String command=requestURI.substring(pos1, pos2);
		
		System.out.println(command);
		
	}

	public List<String> getPostCommand() {
		return postCommand;
	}

	public void setPostCommand(List<String> postCommand) {
		this.postCommand = postCommand;
	}

	public void setDomains(List<String> domains) {
		if(domains != null) {
			for(String domain:domains) {
				this.domains.add(domain);
			}
		}
	}

}
