package com.zengshi.paas.image;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.zengshi.paas.utils.FileUtil;




public class StaticHtmlServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1594325791647123L;
	private static final Logger log = Logger.getLogger(StaticHtmlServlet.class);


	
	// 页面直接显示  /static/html/id
	// 附件下载  /static/attch/id
	@Override
	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
		String uri = request.getRequestURI();
		responseMongoHtml(request,response,uri);
	}
	
	private void responseMongoHtml(HttpServletRequest request,
			HttpServletResponse response, String uri) {
		//直接返回mongoDB中的html信息
		String name = getHtmlName(uri);
		log.debug(uri+"--service------------------");
		if(name == null || name.length() == 0)
			return;
		byte[] htmls = FileUtil.readFile(name);
		//返回html字节流
		if (htmls!=null&&htmls.length>0) {
			PrintWriter writer = null;
			ServletOutputStream out = null;
     		try {
     			response.setHeader("Pragma", "No-cache");  
     	        response.setHeader("Cache-Control", "no-cache");  
     	        response.setDateHeader("Expires", 0);  
     	        if(uri.contains("/static/html/")){
     	        	response.setContentType("text/html;charset=UTF-8");
     	        	writer = response.getWriter();
         			Map<String,String> map = new HashMap<String,String>();   
         	        map.put("result", new String(htmls));  
         	        JSONObject resultJSON = JSONObject.fromObject(map); //根据需要拼装json  
        	        String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数  
        	        //xss过滤   --modify by wenyf 20181005 加上这个过滤前台富文本框显示不出内容，故注释掉
        	        //jsonpCallback = xssEncode(jsonpCallback);
        	        writer.print(jsonpCallback+"("+resultJSON.toString()+")");
        	        writer.flush();
     	        }else if(uri.contains("/static/attch/")){
     	        	response.setContentType("application/ms-excel;charset=UTF-8");
     	        	response.setHeader("Content-Disposition", "attachment; filename ="+
     	        			getFileName(request,name));
     	        	out = response.getOutputStream();
     	        	out.write(htmls);
     	        	out.flush();
     	        }
    			htmls = null;
    			log.debug(uri+"--return------------------ok");
     		} catch (Exception e) {
				log.error("",e);
     		}finally{
     			try {
					if(writer!=null){
						writer.close();
					}
				} catch (Exception e) {
					log.error("",e);			
				}
     			try {
     				if(out!=null){
     					out.close();
     				}
     			} catch (Exception e) {
     				log.error("",e);			
     			}
		
     		}
     	}
	}
	private String getFileName(HttpServletRequest request,String name) {
		String reqStr = request.getHeader("user-agent");
		String res = "";
			try {
				if(reqStr!=null && (reqStr.contains("MSIE") || reqStr.contains("Trident"))){
//					res = new String(FileUtil.getFileName(name).getBytes("GBK"),"ISO8859-1");
					res=java.net.URLEncoder.encode(FileUtil.getFileName(name), "UTF-8");
				}
				else{
					res = new String(FileUtil.getFileName(name).getBytes("UTF-8"),"ISO8859-1");
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		return res;
	}
	private String getHtmlName(String uri) {
		if (uri == null)
			return null;
		String str = null;
		if (uri.contains("?")) {
			uri = uri.substring(0, uri.indexOf("?"));
		}
		if (uri.contains("/")) {
			String[] urls = uri.split("/");
			str = urls[urls.length - 1];
		}else{
			str = uri;
		}
		if (str != null&&str.contains(".")) {
			str = str.substring(0, str.indexOf("."));
		}
		return str;
	}

	/** 
     * 将容易引起xss & sql漏洞的半角字符直接替换成全角字符 
     *  
     * @param s 
     * @return 
     */  
    private  String xssEncode(String s) {  
        if (s == null || s.isEmpty()) {  
            return s;  
        }else{  
            s = stripXSSAndSql(s);  
        }       
        s = s.replace("\n", "" );       
        s = s.replace("\r", "" );        
        StringBuilder sb = new StringBuilder(s.length() + 16);  
        for (int i = 0; i < s.length(); i++) {  
            char c = s.charAt(i);  
            switch (c) {  
            case '>':  
                sb.append("＞");// 转义大于号  
                break;  
            case '<':  
                sb.append("＜");// 转义小于号  
                break;  
            case '\'':  
                sb.append("＇");// 转义单引号  
                break;  
            case '\"':  
                sb.append("＂");// 转义双引号  
                break;  
            case '&':  
                sb.append("＆");// 转义&  
                break;  
            case '#':  
                sb.append("＃");// 转义#  
                break;  
            default:  
                sb.append(c);  
                break;  
            }  
        }  
        return sb.toString();  
    } 
    
    private  String stripXSSAndSql(String value) {  
        if (value != null) {  

            Pattern scriptPattern = Pattern.compile("<[\r\n| | ]*script[\r\n| | ]*>(.*?)</[\r\n| | ]*script[\r\n| | ]*>", Pattern.CASE_INSENSITIVE);  
            value = scriptPattern.matcher(value).replaceAll("");  
           
            scriptPattern = Pattern.compile("src[\r\n| | ]*=[\r\n| | ]*[\\\"|\\\'](.*?)[\\\"|\\\']", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);  
            value = scriptPattern.matcher(value).replaceAll("");  
            
            scriptPattern = Pattern.compile("</[\r\n| | ]*script[\r\n| | ]*>", Pattern.CASE_INSENSITIVE);  
            value = scriptPattern.matcher(value).replaceAll("");  
            
            scriptPattern = Pattern.compile("<[\r\n| | ]*script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);  
            value = scriptPattern.matcher(value).replaceAll("");  
            
            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);  
            value = scriptPattern.matcher(value).replaceAll("");  
            
            scriptPattern = Pattern.compile("e-xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);  
            value = scriptPattern.matcher(value).replaceAll("");  
            
            scriptPattern = Pattern.compile("javascript[\r\n| | ]*:[\r\n| | ]*", Pattern.CASE_INSENSITIVE);  
            value = scriptPattern.matcher(value).replaceAll("");  
            
            scriptPattern = Pattern.compile("vbscript[\r\n| | ]*:[\r\n| | ]*", Pattern.CASE_INSENSITIVE);  
            value = scriptPattern.matcher(value).replaceAll("");  
            
            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);  
            value = scriptPattern.matcher(value).replaceAll("");  
            
            scriptPattern = Pattern.compile("&#x([0-9a-f]+);?");
            value = scriptPattern.matcher(value).replaceAll("");  
            
            scriptPattern = Pattern.compile("%([0-9a-f]{2});?");
            value = scriptPattern.matcher(value).replaceAll(""); 
            
            scriptPattern = Pattern.compile("([a-z0-9]+)=([\"'])(.*?)\\2", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll(""); 
            
            scriptPattern = Pattern.compile("([a-z0-9]+)(=)([^\"\\s']+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll(""); 
            
            scriptPattern = Pattern.compile("&#(\\d+);?");
            value = scriptPattern.matcher(value).replaceAll(""); 
            
            scriptPattern = Pattern.compile("^/([a-z0-9]+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll(""); 
            
            scriptPattern = Pattern.compile("^([a-z0-9]+)(.*?)(/?)$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll(""); 
            
            scriptPattern = Pattern.compile("&([^&;]*)(?=(;|&|$))");
            value = scriptPattern.matcher(value).replaceAll(""); 
            
            scriptPattern = Pattern.compile("(>|^)([^<]+?)(<|$)", Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll(""); 
            
            scriptPattern = Pattern.compile("^>");
            value = scriptPattern.matcher(value).replaceAll(""); 
            
            scriptPattern = Pattern.compile("<([^>]*?)(?=<|$)");
            value = scriptPattern.matcher(value).replaceAll(""); 
            
            scriptPattern = Pattern.compile("(^|>)([^<]*?)(?=>)");
            value = scriptPattern.matcher(value).replaceAll(""); 
            
            scriptPattern = Pattern.compile("<([^>]*?)(?=<|$)");
            value = scriptPattern.matcher(value).replaceAll(""); 
            
            scriptPattern = Pattern.compile("(^|>)([^<]*?)(?=>)");
            value = scriptPattern.matcher(value).replaceAll(""); 
            
            scriptPattern = Pattern.compile("&");
            value = scriptPattern.matcher(value).replaceAll(""); 
            
            scriptPattern = Pattern.compile("\"");
            value = scriptPattern.matcher(value).replaceAll(""); 
            
            scriptPattern = Pattern.compile("<");
            value = scriptPattern.matcher(value).replaceAll(""); 
            
            scriptPattern = Pattern.compile(">");
            value = scriptPattern.matcher(value).replaceAll(""); 
            
            scriptPattern = Pattern.compile("<>");
            value = scriptPattern.matcher(value).replaceAll(""); 
            
        }  
        return value;  
    }  

}
