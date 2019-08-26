/**
 * 
 */
package com.zengshi.butterfly.core.service;

import java.util.HashMap;
import java.util.Map;



/**
 *
 */
public class DefaultURLBrokenImpl  implements URLBroken {

	private String suffix;
	
	private String prefix;
	
	
	
	protected Map<String, String> urlConfig=new HashMap<String, String>();
	
	public String get(String url_key) {
		return this.urlConfig.get(url_key);
	}
	
	@Override
	public void put(String key, String value) {
		// TODO Auto-generated method stub
		 this.urlConfig.put(key, value);
	}
	/*public void put(String url_key,String url) {
		this.put(url_key, url);
	}*/
	/* (non-Javadoc)
	 * @see com.zengshi.butterfly.core.service.URLBroken#build(java.lang.String, java.lang.String)
	 */
	@Override
	public String build(String resourceURL, String command) {
		StringBuffer urlBuffer=new StringBuffer();
		if(prefix != null) {
			urlBuffer.append(prefix);
		}
		
		if(resourceURL != null) {
			urlBuffer.append(resourceURL);
		}
		
		if(command != null) {
			urlBuffer.append(command);
		}
		
		if(suffix != null) {
			urlBuffer.append(suffix);
		}
		
		return urlBuffer.toString();
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public static void main(String[] arg) {
		DefaultURLBrokenImpl broken = new DefaultURLBrokenImpl();
		
		broken.setPrefix("/");
		broken.setSuffix(".html");
		//System.out.println(broken.build("/user", "/add"));
		
		
		String str = "/system//test////show.html";
		StringBuffer sb=new StringBuffer();
		System.out.println(str.replaceAll("\\/{1,}", "/"));
		/*sb.append(str.charAt(0));
		for(int i=0;i<str.length();i++) {
			System.out.println(sb.charAt(i));
			System.out.println(str.charAt(i+1));
			if(sb.charAt(i) == str.charAt(i+1) && sb.charAt(i) == '/') {
				i++;
			}else {
				sb.append(str.charAt(i));
			}
			
		}
		System.out.println( sb.toString());*/
		
	}

	public void setUrlConfig(Map<String, String> urlConfig) {
		this.urlConfig = urlConfig;
	}



}
