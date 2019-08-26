package com.zengshi.paas.image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.zengshi.paas.utils.ImageUtil;
import com.zengshi.paas.utils.StringUtil;

/**
 * 图片服务器
 * 上传图片时，处理图片格式（统一使用jpg格式），再保存到mongoDB
 *
 */
public class UpImageServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -914574498046477046L;
	private static final Logger log = Logger.getLogger(UpImageServlet.class);



	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		HttpServletRequest request = (HttpServletRequest)arg0;
		HttpServletResponse response = (HttpServletResponse)arg1;
		
		boolean success = true; 
		JSONObject json = new JSONObject();

		log.debug("----保存本地图片----------------");
		String filename = null;
		FileOutputStream fos = null;
		InputStream in = null;
		try {
			in = request.getInputStream();
			filename = request.getHeader("filename");
			File f1 = new File(ImageUtil.getUpPath(), filename);
			fos = new FileOutputStream(f1);
			byte[] buffer = new byte[1024];
			int bytes = 0;
			while ((bytes = in.read(buffer)) != -1) {
				fos.write(buffer, 0, bytes);
			}
			fos.flush();
		} catch (Exception e) {
			log.error("图片保存到本地出错：", e);
			success = false;
		} finally {
			fos.close();
			in.close();
		}
		if(success){
			String name = getName();
			String id = "";
			
			/**
			String type = getType(filename);
			if(ImageUtil.getSupportType().equals(type)){
				log.debug("----直接保存到mongoDB----------------");
				try {
					id = ImageUtil.saveToRomte(new File(ImageUtil.getUpPath(),filename), filename, ImageUtil.getSupportType());
				} catch (Exception e) {
					log.error("直接保存到mongoDB出错：", e);
					success = false;
				}
				json.put("id", id);
			}else{
		    	try {
		    		//gm处理
					log.debug("----转化图片格式----------------");
					ImageUtil.convertType(filename,name+ImageUtil.getSupportType());
					log.debug("----保存到mongoDB----------------");
					id = ImageUtil.saveToRomte(new File(getNewPathName(name)), filename, ImageUtil.getSupportType());
					json.put("id", id);
				} catch (Exception e) {
					success = false;
					log.error("图片格式转换、保存到mongodb出错：", e);
				}
			}
			*/
			/**2014-12-08 都做图片去杂质处理
			try {
	    		//gm处理
				log.debug("----转化图片格式----------------");
				String fileType=getType(filename);
				if(StringUtil.isBlank(fileType)){
					fileType=ImageUtil.getSupportType();
				}
				ImageUtil.convertType(filename,name+fileType);
				log.debug("----保存到mongoDB----------------");
				id = ImageUtil.saveToRomte(new File(getNewPathName(name,fileType)), filename,fileType);
				json.put("id", id);
			} catch (Exception e) {
				success = false;
				log.error("图片格式转换、保存到mongodb出错：", e);
			}*/
			
			//2018-09-21 传入图片之后显示不出来bug修订
			try {
                //gm处理
                log.debug("----转化图片格式----------------");
                String fileType=ImageUtil.getSupportType();
                ImageUtil.convertType(filename,name+fileType);
                log.debug("----保存到mongoDB----------------");
                id = ImageUtil.saveToRomte(new File(getNewPathName(name,fileType)), filename,fileType);
                json.put("id", id);
            } catch (Exception e) {
                success = false;
                log.error("图片格式转换、保存到mongodb出错：", e);
            }
		}
		json.put("result", success?"success":"failure");
		response(response,json.toString());
	}
	
	private String getType(String name) {
		if(name!=null)
			return name.substring(name.indexOf(".")).toLowerCase();
		return null;
	}


	private void response(HttpServletResponse response, String result) {
			ServletOutputStream outStream = null;
     		try {
     			response.setHeader("Pragma", "No-cache");  
     	        response.setHeader("Cache-Control", "no-cache");  
     	        response.setDateHeader("Expires", 0);  
     			response.setContentType("text/html;charset=UTF-8");
     			outStream = response.getOutputStream();
     			outStream.print(result);
    			outStream.flush();
    			log.debug("--return------------------ok");
     		} catch (Exception e) {
				log.error("",e);
     		}finally{
     			try {
					if(outStream!=null){
						outStream.close();
					}
				} catch (Exception e) {
					log.error("",e);			
				}
		
     		}
	}


	private String getNewPathName(String name,String fileType) {
		return ImageUtil.getUpPathNew().endsWith(File.separator)?(ImageUtil.getUpPathNew()+name+fileType):
			(ImageUtil.getUpPathNew()+File.separator+name+fileType);
	}


	private String getName() {
		return UUID.randomUUID()+"";
	}
	

}
