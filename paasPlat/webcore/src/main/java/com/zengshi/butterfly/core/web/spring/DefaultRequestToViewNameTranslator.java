/**
 * 
 */
package com.zengshi.butterfly.core.web.spring;


/**
 *
 */
public class DefaultRequestToViewNameTranslator extends
		org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator {

	private String prefix = "";

	private String suffix = "";
	
	public String getViewName(String requestPath) {
		
		return (this.prefix + transformPath(requestPath) + this.suffix);
	}
	
	public void setPrefix(String prefix) {
		this.prefix = (prefix != null ? prefix : "");
	}
	
	public void setSuffix(String suffix) {
		this.suffix = (suffix != null ? suffix : "");
	}
}
