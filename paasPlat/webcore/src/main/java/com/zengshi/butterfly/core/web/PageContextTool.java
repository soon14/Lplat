/**
 * 
 */
package com.zengshi.butterfly.core.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public class PageContextTool {

	private HttpServletRequest request;
	
	private static Set<String> librarys=new HashSet<String>();
	
	private List<String> style;
	
	private List<String> script;
	
	private String title;
	
	private String contextPath;
	
	private List<String> head;
	
	static {
		librarys.add("bootstrap");
		librarys.add("jquery");
		librarys.add("jquery-easyui");
		librarys.add("jquery-ztree");
	}
	
	public void addHeader(String head) {
		if(this.head == null) this.head =new ArrayList<String>();
		this.head.add(head);
	}
	
	public void addScript(String script) {
		if(this.script == null) this.script =new ArrayList<String>();
		this.script.add(script);
	}
	
	public void addStyle(String style) {
		if(this.style == null) this.style =new ArrayList<String>();
		this.style.add(style);
	}
	
	/*public void addModuleScript(String module,String script) {
		if(this.script == null) this.script =new ArrayList<String>();
		StringBuffer javascript=new StringBuffer();
		javascript.append("/").append(module).append(script);
		this.script.add(javascript.toString());
	}
	
	public void addModuleStyle(String module,String style) {
		if(this.style == null) this.style =new ArrayList<String>();
		StringBuffer css=new StringBuffer();
		css.append("/").append(module).append(this.getTheme()).append(style);
		this.style.add(css.toString());
	}
	
	private String getTheme() {
		return "";
	}*/

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public List<String> getStyle() {
		return style;
	}

	public void setStyle(List<String> style) {
		this.style = style;
	}


	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public List<String> getHead() {
		return head;
	}

	public void setHead(List<String> head) {
		this.head = head;
	}

	public List<String> getScript() {
		return script;
	}

	public void setScript(List<String> script) {
		this.script = script;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
