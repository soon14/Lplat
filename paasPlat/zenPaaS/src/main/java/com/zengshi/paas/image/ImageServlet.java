package com.zengshi.paas.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zengshi.paas.utils.FastDFSUtil;
import org.apache.log4j.Logger;

import com.zengshi.paas.utils.ImageUtil;
import com.zengshi.paas.utils.FileUtil;



public class ImageServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1594325791647123L;
	private static final Logger log = Logger.getLogger(ImageServlet.class);
	private DateFormat df = getDateFormat();



	@Override
	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		long begin = System.currentTimeMillis();
		HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
		String mark=request.getParameter("mark");

		String uri = request.getRequestURI();
		log.debug(uri+"--service------------------");
		String imageType = getImageType(uri);
		if(!ImageUtil.supportService(imageType)){
			log.error(uri+"--service------------------资源格式不支持");
			return;
		}
		String imageName = getImageNameOfOld(uri);
		boolean isMark=ImageUtil.isMark(imageName);
		imageName=ImageUtil.parseImageId(imageName);

		if(isCachedByIE(request,response,imageName)){
			log.debug(uri+"--return 304------------------ok");
			//已被浏览器缓存，服务端没有修改，只需要返回304
			return;
		}
		if(ImageUtil.getGMMode()){
			String imageSize = null;
			boolean isExtent = false;
			if(!isSourceImage(uri)){
				imageSize = getImageSize(uri);
				if(imageSize.endsWith("!")){
					isExtent = true;
					imageSize = imageSize.replace("!", "");
				}
					
			}else{
				if(imageName.endsWith("!")){
					isExtent = true;
					imageName = imageName.replace("!", "");
				}
			}
			//得到缩略图
			log.debug(uri+"-----------imageName:"+imageName+"   ----------imageSize:"+imageSize);
			String imagePath = "";
			try {
				if(isMark || mark!=null){
					imagePath=ImageUtil.getScaleWatermarkImage(uri,imageName, imageSize,imageType,isExtent);
				}else{
					imagePath = ImageUtil.getScaleImage(uri,imageName, imageSize,imageType,isExtent);
				}
			} catch (Exception e) {
				log.error("",e);
//				imagePath =  ImageUtil.getReservePath()+"error"+((imageSize==null)?".jpg":"_"+imageSize+".jpg");
				imagePath =  ImageUtil.getReservePath()+"error.jpg";
				log.error(uri+"--异常时,返回图片路径------------------"+imagePath);
			}
			//返回图片字节流
			File file = new File(imagePath);
			if (file.exists()) {
				InputStream in = null;
				ServletOutputStream outStream = null;
	     		try {
	     			in = new FileInputStream(file);
	     			if(in != null)
	     				response.setIntHeader("Content-Length", in.available());
	     			outStream = response.getOutputStream();
	     			byte[] data = new byte[2048];
	    			int count = -1;  
	    			while((count = in.read(data,0,2048)) != -1)  
	    				 outStream.write(data, 0, count);  
	    			data = null;
	    			outStream.flush();
	    			log.debug(uri+"--return------------------ok");
	     		}  catch (Exception e) {
					log.error("",e);
	     		}finally{
	     			try {
						if(outStream!=null){
							outStream.close();
						}
					} catch (Exception e) {
						log.error("",e);					
					}
	     			if(in!=null){
	     				in.close();
	     			}
	     		}	
			}
		}else{
			//不需要GM动态产生缩略图
			responseMongoImage(request,response,uri);
		}
		log.debug(uri+"-------------------:共耗时"+(System.currentTimeMillis()-begin)+"ms");
	}


	/**是否已被浏览器缓存，服务端没有修改，只需要返回304
	 * @param request
	 * @param response
	 * @return
	 */
	private boolean isCachedByIE(HttpServletRequest request,
			HttpServletResponse response,String imageName) {
		// 是否已经被IE缓存，没有修改
		boolean isCached = false;
		String modifiedSince = request.getHeader("If-Modified-Since");
		if(df == null)
			df = getDateFormat();
		// Date uploadDate = f.getUploadDate();
		// String lastModified = df.format(uploadDate);
		long update;
		if(FileUtil.fileSysType==2 && imageName.contains("/")){
			update= FastDFSUtil.getFileInfo(imageName).getCreateTimestamp().getTime();
		}else{
			update = getUpdateTime(imageName);
		}
		String lastModified = df.format(new Date(update));
		if (modifiedSince != null) {
			Date modifiedDate = null;
			Date sinceDate = null;
			try {
				modifiedDate = df.parse(lastModified);
				sinceDate = df.parse(modifiedSince);
			} catch (ParseException e) {
				log.error("", e);
			}
			if (modifiedDate.compareTo(sinceDate) <= 0) {
				isCached = true;
				response.setStatus(304); // Not Modified
				return isCached;
			}
		}
		response.setContentType("image/jpeg");
		long maxAge = 10L * 365L * 24L * 60L * 60L; // ten years, in seconds
		response.setHeader("Cache-Control", "max-age=" + maxAge);
		response.setHeader("Last-Modified", lastModified);
		response.setDateHeader("Expires", update + maxAge * 1000L);
		return isCached;
	}


	private DateFormat getDateFormat() {
		DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",
				Locale.ENGLISH);
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		return df;
	}


	private long getUpdateTime(String imageName) {
		Date updateTime = null ;
		try {
			updateTime = FileUtil.readUpdateTime(imageName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return updateTime!=null?updateTime.getTime():1412931179491l;
	}


	/**判断是否是源图
	 * @param uri
	 * @return
	 */
	private boolean isSourceImage(String uri) {
		return !getName(uri).contains("_");
	}


	private void responseMongoImage(HttpServletRequest request,
			HttpServletResponse response, String uri) {
		//直接返回mongoDB中的图（源图＋缩略图  都先存储在mongoDB）
		String name = getImageName(uri);
		log.debug(uri+"--service--------------不使用GM动态生成缩略图----");
		byte[] images = FileUtil.readFile(name);
		//返回图片字节流
		if (images!=null&&images.length>0) {
			ServletOutputStream outStream = null;
     		try {
     			response.setIntHeader("Content-Length", images.length);
     			outStream = response.getOutputStream();
    			outStream.write(images, 0, images.length);  
    			outStream.flush();
    			images = null;
    			log.debug(uri+"--return------------------ok");
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
	}



	private String getImageSize(String uri) {
		if (uri == null)
			return null;
		String image = null;
		if (uri.contains("?")) {
			uri = uri.substring(0, uri.indexOf("?"));
		}
		if (uri.contains("/")) {
			String[] urls = uri.split("/");
			image = urls[urls.length - 1];
		}else{
			image = uri;
		}
		if (image != null) {
			if (image.contains("_")) {
				image = image.substring(image.indexOf("_")+1,
						image.indexOf("."));
			}
		}
		return image;
	}

	private String getImageNameOfOld(String uri){
		if (uri == null)
			return null;
		String image = null;
		if (uri.contains("?")) {
			uri = uri.substring(0, uri.indexOf("?"));
		}
		if (uri.contains("/")) {
			String[] urls = uri.split("image/");
			image = urls[urls.length - 1];
		}else{
			image = uri;
		}
		if (image != null) {
			String oimage=image;
			int idx=image.indexOf(".");
			if (image.contains("_")) {
				image = image.substring(0, image.indexOf("_"));
			} else {
				image = image.substring(0, idx);
			}
			if(FileUtil.fileSysType==2 && idx>0 && image.contains("/")){//判断存储方式为fastdfs
				image=image+oimage.substring(idx);
			}
		}
		return image;
	}

	private String getImageName(String uri) {
		String image=getImageNameOfOld(uri);
		image=ImageUtil.parseImageId(image);//补充加密设置

		return image;
	}

	private String getName(String uri) {
		if (uri == null)
			return null;
		String image = null;
		if (uri.contains("?")) {
			uri = uri.substring(0, uri.indexOf("?"));
		}
		if (uri.contains("/")) {
			String[] urls = uri.split("/");
			image = urls[urls.length - 1];
		}else{
			image = uri;
		}
		return image;
	}
	private String getImageType(String uri) {
		if (uri == null)
			return null;
		String image = null;
		if (uri.contains("?")) {
			uri = uri.substring(0, uri.indexOf("?"));
		}
		if (uri.contains("/")) {
			String[] urls = uri.split("/");
			image = urls[urls.length - 1];
		}else{
			image = uri;
		}
		if (image != null) {
			if (image.contains(".")) {
				image = image.substring(image.indexOf(".")).toLowerCase();
			}else{
				image = null;
			}
		}
		return image;
	}
	
}
