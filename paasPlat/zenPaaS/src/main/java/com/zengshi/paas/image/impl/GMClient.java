package com.zengshi.paas.image.impl;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.gm4java.engine.GMConnection;
import org.gm4java.engine.GMService;
import org.gm4java.engine.support.GMConnectionPoolConfig;
import org.gm4java.engine.support.PooledGMService;
import org.springframework.util.StringUtils;

import com.zengshi.paas.utils.CacheUtil;
import com.zengshi.paas.utils.FileUtil;
import com.zengshi.paas.utils.StringUtil;
import com.gif4j.GifDecoder;
import com.gif4j.GifEncoder;
import com.gif4j.GifTransformer;

public class GMClient {

    private static final Logger log = Logger.getLogger(GMClient.class);

    private GMService gMService;

    private static final String GM_PATH_KEY = "gmPath";
    private static final String MAX_ACTIVE_KEY = "maxActive";
    private static final String MAX_WAIT_KEY = "maxWait";
    private static final String MAX_IDLE_KEY = "maxIdle";
    private static final String TEST_ON_BORROW_KEY = "testOnBorrow";
    private static final String TEST_ON_RETURN_KEY = "testOnReturn";
    private static final String IMAGE_ROOT = "imageRoot";
    private static final String IMAGE_ROOT_NEW = "imageRootNew";
    private static final String IMAGE_NAME_SPLIT = "imageNameSplit";
    private static final String EXTENT_KEY = "extent";
    private static final String EXTENT_COLOR_KEY = "extentColor";
    private static final String QUALITY_KEY = "quality";

    private static final String REDISKEYPREFIX_KEY = "redisKeyPrefix";
    private static final String VARNISHSERVER_KEY = "varnishServer";
    private static final String COMMANDSIZE_KEY = "commandSize";
    private static final String PRIMARYPARAM_KEY = "primaryParam";
    private static final String LOGO_FILE_KEY="logoFile";//logo水印图片文件
    private static final String IMAGE_MARK_ROOT_NEW="imageMarkRootNew";//打水印文件路径
    private static final String LOGO_POSITION_KEY="logoPosition";//水印位置 NorthWest, North, NorthEast, West, Center, East, SouthWest, South, SouthEast


    //本地保存图片的路径 源图
    private String imageRoot = null;
    //图片名分隔符 _
    private String imageNameSplit = null;
    //本地保存图片的路径 缩略图
    private String imageRootNew = null;
    //本地保存打水印图片的路径 缩略图
    private String imageMarkRootNew = null;
    //水印位置
    private String logoPosition="Center";//EN:右下角
    //水印图片宽
    private int logWidth=0;
    //水印图片高
    private int logHeight=0;
    //是否补白尺寸图
    private boolean extent = true;
    //补白颜色
    private String extentColor="white";
    //图片质量,默认图片质量为 80
    private int quality = 80;

    //图片缓存入redis，前缀
    private String redisKeyPrefix;
    //varnish服务器ip、port
    private String varnishServer;
    //varnish服务器ip、port
    private JSONArray varnishServers;
    //与varnish一次连接，清除缓存url的个数
    private int commandSize;
    //url中关键参数名称 如：productId
    private String primaryParam;
    //水印logo文件
    private String logoFile;

    public GMClient(String parameter) {
        try {
            JSONObject json = JSONObject.fromObject(parameter);
            if (json != null) {
                GMConnectionPoolConfig config = new GMConnectionPoolConfig();
                config.setMaxActive(json.getInt(MAX_ACTIVE_KEY));
                config.setMaxIdle(json.getInt(MAX_IDLE_KEY));
                config.setMaxWait(json.getLong(MAX_WAIT_KEY));
                config.setTestOnGet(json.getBoolean(TEST_ON_BORROW_KEY));
                config.setTestOnReturn(json.getBoolean(TEST_ON_RETURN_KEY));
                config.setGMPath(json.getString(GM_PATH_KEY));
                gMService = new PooledGMService(config);

                imageRoot = json.getString(IMAGE_ROOT);
                imageNameSplit = json.getString(IMAGE_NAME_SPLIT);
                imageRootNew = json.getString(IMAGE_ROOT_NEW);
                extent = json.getBoolean(EXTENT_KEY);
                if(json.containsKey(EXTENT_COLOR_KEY)){
                    extentColor=json.getString(EXTENT_COLOR_KEY);
                }
                if(json.containsKey(LOGO_POSITION_KEY)){
                    logoPosition=json.getString(LOGO_POSITION_KEY);
                }
                if(json.containsKey(IMAGE_MARK_ROOT_NEW)){
                    imageMarkRootNew=json.getString(IMAGE_MARK_ROOT_NEW);
                }
                if(json.containsKey(LOGO_FILE_KEY)){
                    logoFile=json.getString(LOGO_FILE_KEY);
                    if(StringUtils.hasText(logoFile)){
                        File file=new File(logoFile);
                        if(file.exists()){
                            BufferedImage bufferedImage = ImageIO.read(file);
                            logWidth = bufferedImage.getWidth();
                            logHeight = bufferedImage.getHeight();
                        }
                    }
                }

                if(json.containsKey(QUALITY_KEY)){
                    try{
                       int tmp = json.getInt(QUALITY_KEY);
                       if(tmp > 0){
                           quality = json.getInt(QUALITY_KEY);
                       }
                    }catch(Exception err){
                        log.error("输入的"+QUALITY_KEY+"的值，无法转为数字，请检查ZK配置");
                    }
                }

                redisKeyPrefix = json.getString(REDISKEYPREFIX_KEY);
                varnishServer = json.getString(VARNISHSERVER_KEY);
                if (varnishServer != null) {
                    varnishServers = JSONArray.fromObject(varnishServer);
                }
                commandSize = json.getInt(COMMANDSIZE_KEY);
                primaryParam = json.getString(PRIMARYPARAM_KEY);
            }

        } catch (Exception e) {
            log.error("", e);
        }
    }

    /**
     * @param imageName 源图名
     * @return
     * @throws Exception
     */
    public boolean localImageExist(String imageName, String imageType) throws Exception {
        if (imageName == null)
            return false;
        String nimageId=imageName;
        if(FileUtil.fileSysType==2 && imageName.contains("/")){
            nimageId=nimageId.substring(nimageId.lastIndexOf("/")+1,nimageId.indexOf("."));
        }
        String localPath = imageRoot + (imageRoot.endsWith(File.separator) ? "" : File.separator) + getFirstPath(nimageId) + File.separator + getSecondPath(nimageId);
        forceMkdir(new File(localPath));
        log.debug("------------------------localPath----------------------" + localPath);
        return new File(localPath + File.separator + nimageId + imageType).exists();
    }

    public String getSourceImagePath(String imageName, String imageType) throws Exception {
        String nimageId=imageName;
        if(FileUtil.fileSysType==2 && imageName.contains("/")){
            nimageId=nimageId.substring(nimageId.lastIndexOf("/")+1,nimageId.indexOf("."));
        }
        return (imageRoot + (imageRoot.endsWith(File.separator) ? "" : File.separator) + getFirstPath(nimageId) + File.separator + getSecondPath(nimageId) + File.separator + nimageId + imageType);
    }

    /**
     * @param imageId 源图名
     * @throws Exception
     */
    public void getRomteImage(String imageId, String imageType) throws Exception {
        String nimageId=imageId;
        if(FileUtil.fileSysType==2 && imageId.contains("/")){
            nimageId=nimageId.substring(nimageId.lastIndexOf("/")+1,nimageId.indexOf("."));
        }
        String destDir=imageRoot + (imageRoot.endsWith(File.separator) ? "" : File.separator) + getFirstPath(nimageId) + File.separator + getSecondPath(nimageId);
        String destFile=destDir + File.separator + nimageId + imageType;
        String tempFile=destDir+ File.separator + "tmp_" + UUID.randomUUID().toString() + imageType;//解决多线程读取文件，导致文件内容不一致
        FileUtil.readFile(imageId, tempFile);
        File file=new File(tempFile);
        File dest = new File(destFile);
        if(dest.exists()){
            log.debug(dest.getName() + "---------- is exists , no renameTo---------:" + dest.getName() +";" );
            file.delete();
        } else{
            boolean flag=file.renameTo(dest);//还是会存在并发的可能
            log.debug(file.getName() + "--------renameTo---------:" + dest.getName() +";" + flag );
            if(!flag){
                file.delete();
            }
        }

    }
    /**

     * 在图片中加水印

     * @param src

     * @param dest

     * @throws IOException

     */
    private void addTextWatermarkToGif(File src,  File dest)throws IOException {
    	BufferedImage bufferedImage = ImageIO.read(new File(logoFile));
        //图片对象
        com.gif4j.GifImage gf = GifDecoder.decode(src);
        //获取图片大小
        int iw = gf.getScreenWidth();
        int ih = gf.getScreenHeight();
        //获取水印大小
        int tw = bufferedImage.getWidth();
        int th = bufferedImage.getHeight();
        //水印位置
        Point p = new Point();
        p.x = iw - tw - 5;
        p.y = ih - th - 4;
        //加水印
        com.gif4j.Watermark watermark = new com.gif4j.Watermark(bufferedImage, p);
        gf = watermark.apply(GifDecoder.decode(src), true);
        //输出
        GifEncoder.encode(gf, dest);
    }
    /** 
     *GIF文件缩略图处理函数 
     *srcImg 源图  
     *destImg 缩略图 
     */  
	private void getGifImage(File srcImg, File destImg, int formatWideth,int formatHeight, boolean smooth) {
		try {
			com.gif4j.GifImage gifImage = GifDecoder.decode(srcImg);
			int imageWideth = gifImage.getScreenWidth();
			int imageHeight = gifImage.getScreenHeight();
			int changeToWideth = formatWideth;
			int changeToHeight = formatHeight;
			if (imageWideth > 0 && imageHeight > 0) {
				// flag=true;
				if (imageWideth / imageHeight >= formatWideth / formatHeight) {
					if (imageWideth > formatWideth) {
						changeToWideth = formatWideth;
						changeToHeight = (imageHeight * formatWideth)
								/ imageWideth;
					} else {
						changeToWideth = imageWideth;
						changeToHeight = imageHeight;
					}
				} else {
					if (imageHeight > formatHeight) {
						changeToHeight = formatHeight;
						changeToWideth = (imageWideth * formatHeight)
								/ imageHeight;
					} else {
						changeToWideth = imageWideth;
						changeToHeight = imageHeight;
					}
				}
			}
			com.gif4j.GifImage resizedGifImage2 = GifTransformer.resize(
					gifImage, changeToWideth, changeToHeight, smooth);
			GifEncoder.encode(resizedGifImage2, destImg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 取图片帧数，非gif返回0
	 * @param filePath
	 * @return
	 */
	private int getFrameCount(String filePath){
		int frameCount=0;//帧数
    	try {
    		com.gif4j.GifImage gifImage2 = GifDecoder.decode(new File(filePath));
    		frameCount=gifImage2.getNumberOfFrames();
//    		GifImage gifImage=new GifImage(sourceImage);
//    		FrameCount=gifImage.getFrameCount();
    	} catch (IOException e) {
    		
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return frameCount;
	}
    public String scaleImage(String uri, String imageName, int type,
                             String imageSize, String imageType, boolean isExtent) throws Exception {
        long begin = System.currentTimeMillis();
//        System.out.println("----uri:"+uri);
//        System.out.println("----imageName:"+imageName);
//        System.out.println("----type:"+type);
//        System.out.println("----imageSize:"+imageSize);
//        System.out.println("----imageType:"+imageType);
//        System.out.println("----isExtent:"+isExtent);
        log.debug(uri + "----GraphicsImage----scaleImage---------begin");
        String nimageId=imageName;
        if(FileUtil.fileSysType==2 && imageName.contains("/")){
            nimageId=nimageId.substring(nimageId.lastIndexOf("/")+1,nimageId.indexOf("."));
        }
        String newPath = imageRootNew + (imageRootNew.endsWith(File.separator) ? "" : File.separator) + getFirstPath(nimageId) + File.separator + getSecondPath(nimageId) + File.separator + nimageId + imageNameSplit + imageSize + imageType;
        if(new File(newPath).exists()){
            return newPath;
        }
        forceMkdir(new File(imageRootNew + (imageRootNew.endsWith(File.separator) ? "" : File.separator) + getFirstPath(nimageId) + File.separator + getSecondPath(nimageId)));
        GMConnection connection = null;
        try {
        	int frameCount=0;//帧数
    		String sourceImage=getSourceImagePath(imageName,imageType);
    		frameCount=this.getFrameCount(sourceImage);
//        	System.out.println("----FrameCount:"+FrameCount);
//          if(imageType.indexOf("gif")!=-1){
        	if(frameCount>1){//实为gif图，只是后缀不为gif
        		newPath = imageRootNew + (imageRootNew.endsWith(File.separator) ? "" : File.separator) + getFirstPath(nimageId) + File.separator + getSecondPath(nimageId) + File.separator + nimageId + imageNameSplit + imageSize + ".gif";
//            	System.out.println("----sourceImage:"+sourceImage);
//            	System.out.println("----newPath:"+newPath);
            	File srcImg=new File(sourceImage);
            	File destImg=new File(newPath);
            	com.gif4j.GifImage gifImage = GifDecoder.decode(srcImg);
    			int imageWideth = gifImage.getScreenWidth();
    			int imageHeight = gifImage.getScreenHeight();
    			if(StringUtil.isNotBlank(imageSize)){
    				String width = imageSize.substring(0, imageSize.indexOf("x"));
    				String height = imageSize.substring(imageSize.indexOf("x")+1,imageSize.length());
    				imageWideth=StringUtil.isBlank(width)?imageWideth:new Integer(width);
    				imageHeight=StringUtil.isBlank(height)?imageHeight:new Integer(height);
    			}
                getGifImage(srcImg, destImg, imageWideth, imageHeight, true);
            }else{
            	String command = getCommand(imageName, type, imageSize, newPath, imageType, isExtent);
//            	System.out.println("----command:"+command);
            	log.debug(uri + "----GraphicsImage----scaleImage---------command:" + command);
            	connection = gMService.getConnection();
            	connection.execute(command);
            }
        } catch (Exception e) {
            log.error("", e);
            throw e;
        } finally {
            if (connection != null)
                connection.close();
        }
        log.debug(uri + "----GraphicsImage----scaleImage---------end 耗时" + (System.currentTimeMillis() - begin));
        log.debug(uri + "----GraphicsImage----newPath---------" + newPath);
        return newPath;
    }

    public String scaleWatermarkImage(String uri, String imageName,String imageSize, String imageType, boolean isExtent) throws Exception {
        long begin = System.currentTimeMillis();
        log.debug(uri + "----GraphicsImage----scaleImage---------begin");
        String nimageId=imageName;
        if(FileUtil.fileSysType==2 && imageName.contains("/")){
            nimageId=nimageId.substring(nimageId.lastIndexOf("/")+1,nimageId.indexOf("."));
        }
        String newSourcePath = imageMarkRootNew + (imageMarkRootNew.endsWith(File.separator) ? "" : File.separator) + getFirstPath(nimageId) + File.separator + getSecondPath(nimageId) + File.separator + nimageId + imageType;
        String newPath = imageMarkRootNew + (imageMarkRootNew.endsWith(File.separator) ? "" : File.separator) + getFirstPath(nimageId) + File.separator + getSecondPath(nimageId) + File.separator + nimageId + imageNameSplit + imageSize + imageType;
        if(new File(newPath).exists()){
            return newPath;
        }
        forceMkdir(new File(imageMarkRootNew + (imageMarkRootNew.endsWith(File.separator) ? "" : File.separator) + getFirstPath(nimageId) + File.separator + getSecondPath(nimageId)));
        GMConnection connection = null;
        try {
            connection = gMService.getConnection();
            int frameCount=0;//帧数
    		String sourceImage=getSourceImagePath(imageName,imageType);
    		frameCount=this.getFrameCount(sourceImage);
//    		System.out.println("----frameCount:"+frameCount);
            if(frameCount>1){
//            	System.out.println("----newSourcePath_Watermark_begin:"+newSourcePath);
//         		System.out.println("----newPath_Watermark_begin:"+newPath);
            	if(!new File(newSourcePath).exists()){
             		newPath= scaleImage(uri, imageName, 1, imageSize, imageType, isExtent);
             		newSourcePath=newPath;
             		addTextWatermarkToGif(new File(newPath), new File(newPath));
//             		System.out.println("----newSourcePath_Watermark:"+newSourcePath);
//             		System.out.println("----newPath_Watermark:"+newPath);
            	}
            }else{
        		if(!new File(newSourcePath).exists()){
//             		System.out.println("----newSourcePath_Watermark:"+newSourcePath);
//             		System.out.println("----newPath_Watermark:"+newPath);
             		String command = getCommandOfWatermark(imageName, newSourcePath, imageType);
             		log.debug(uri + "----GraphicsImage----scaleImage---------command:" + command);
             		connection.execute(command);
         		}
            	if(null!=imageSize){
            		 String scaleCommand = getCommand(imageName, 0, imageSize,newSourcePath, newPath, imageType, isExtent);
            		 connection.execute(scaleCommand);
            	}
            }
        } catch (Exception e) {
            log.error("", e);
            throw e;
        } finally {
            if (connection != null)
                connection.close();
        }
        log.debug(uri + "----GraphicsImage----scaleImage---------end 耗时" + (System.currentTimeMillis() - begin));
        log.debug(uri + "----GraphicsImage----newPath---------" + newPath);
        return null!=imageSize?newPath:newSourcePath;
    }

    public void addImgText(String srcPath, String newPath) throws Exception {
        //暂时不实现

    }


    @SuppressWarnings("resource")
    public void saveCacheImage(String imageName, String imageSize,
                               String newPath, String imageType) throws Exception {
        File file = new File(newPath);
        String nimageId=imageName;
        if(FileUtil.fileSysType==2 && imageName.contains("/")){
            nimageId=nimageId.substring(nimageId.lastIndexOf("/")+1,nimageId.indexOf("."));
        }
        if (file.exists()) {
            InputStream input = new FileInputStream(file);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] data = new byte[2048];
            int count;
            while ((count = input.read(data, 0, 2048)) != -1){
                outStream.write(data, 0, count);
            }
            CacheUtil.addItemFile(nimageId + imageNameSplit + imageSize + imageType, outStream.toByteArray());
            outStream.flush();
            outStream.close();
        }

    }

    public void removeLocalCutedImage(String path) throws Exception {
        File file = new File(path);
        if (file.exists()) {
            if (file.isFile()) {
                boolean result = false;
                int tryCount = 0;
                while (!result && tryCount++ < 3) {
                    System.gc();
                    result = file.delete();
                }
            }
            file.delete();
        } else {
            System.out.println("所删除的文件不存在！");
        }
    }

    public String saveToRomte(File image, String imageName, String imageType) throws Exception {
        if (image != null && imageName != null && imageType != null) {
            FileInputStream input = null;
            try {
                input = new FileInputStream(image);
                byte[] datas = new byte[input.available()];
                input.read(datas);
                return FileUtil.saveFile(datas, imageName, imageType);
            } catch (Exception e) {
                log.error("保存图片失败", e);
                throw e;
            } finally {
                if (input != null)
                    input.close();
            }
        } else {
            throw new Exception("参数非法，要求都不为空！");
        }
    }

    public boolean removeCacheHtml(String productId) throws Exception {
        if (productId == null || "".equals(productId)) {
            log.error("入参商品ID为空");
            return false;
        }

        Set<String> redisKeys = getRedisKey(productId);

        //清除varnish缓存
        Set<String> varnishUrlKeys = getVarnishUrlKey(redisKeys, productId);
        log.debug("----------------varnishUrlKeys缓存标识-----------" + varnishUrlKeys);
        if (varnishUrlKeys != null && varnishUrlKeys.size() > 0)
            delVarnishItem(varnishUrlKeys);

        //清除redis缓存
        log.debug("----------------redisKeys缓存标识-----------" + redisKeys);
        if (redisKeys != null && redisKeys.size() > 0) {
            for (String key : redisKeys)
                CacheUtil.delItem(key);
        }

        return true;
    }

    private void delVarnishItem(Set<String> varnishUrlKeys) {
//		key = "^/woego/image/53ac100a300479425ec1f9a0.jpg.*$";
        int connectSize = varnishUrlKeys.size() % commandSize == 0 ? varnishUrlKeys.size() / commandSize : (varnishUrlKeys.size() / commandSize + 1);
        String[] keys = varnishUrlKeys.toArray(new String[varnishUrlKeys.size()]);
        for (int i = 1; i <= connectSize; i++) {
            if (varnishServers != null && varnishServers.size() > 0) {
                for (int j = 0; j < varnishServers.size(); j++) {
                    String ip = ((JSONObject) varnishServers.get(j)).getString("host");
                    int port = ((JSONObject) varnishServers.get(j)).getInt("port");
                    delVarnishItemPre(ip, port, keys, i);
                }
            }
        }

    }

    /**
     * 每次与varnish管理端口建立连接，执行删除缓存
     *
     * @param ip
     * @param port
     * @param varnishUrlKeys
     * @param i
     */
    private void delVarnishItemPre(String ip, int port, String[] varnishUrlKeys, int i) {
        Socket socket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 5000);
            socket.setSendBufferSize(1000);
            socket.setSoTimeout(5000);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());
            for (int k = 0; k < commandSize && k < varnishUrlKeys.length; k++) {
                String url = varnishUrlKeys[commandSize * (i - 1) + k];
                String baseCMD = "ban";
                String command = baseCMD + ".url " + url;
                writer.println(command);
                writer.flush();
                String result;
                while ((result = reader.readLine()) != null) {
                    result = result.trim();
                    log.debug("----command------" + command + "---------------------" + result);
                    if (result.equals("200 0")) {
                        break;
                    }
                }
            }

        } catch (Exception e) {
            log.error("", e);
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (Exception e) {
                log.error("", e);
            }
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                log.error("", e);
            }
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                log.error("", e);
            }
        }

    }

    private Set<String> getVarnishUrlKey(Set<String> keys, String productId) {
        Set<String> res = new HashSet<String>();
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                //TODO rediskey-->url  带上产品ID  产品ID必须
                key = key.replace(redisKeyPrefix, "^");
                if (key.contains(".._" + productId)) {
                    if (key.indexOf(".._" + productId) == key.indexOf(".._"))
                        key = key.substring(0, key.indexOf(".._" + productId)) + "\\?" + primaryParam + "=" + productId;
                    else
                        key = key.substring(0, key.indexOf(".._" + productId)) + primaryParam + "=" + productId;
                }
                if (key.contains(".._"))
                    key = key.replace(".._", ".*");
                key += ".*$";
                res.add(key);
            }
        }
        return res;
    }

    /**
     * 创建目录
     *
     * @param directory
     * @throws IOException
     */
    private void forceMkdir(File directory) throws IOException {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                String message = "文件 " + directory + " 存在，不是目录。不能创建该目录。 ";
                throw new IOException(message);
            }
        } else {
            if (!directory.mkdirs()) {
                if (!directory.isDirectory()) {
                    String message = "不能创建该目录 " + directory;
                    throw new IOException(message);
                }
            }
        }
    }

    private Set<String> getRedisKey(String productId) {
        return CacheUtil.getSetForStatic(redisKeyPrefix + productId);
    }

    private String getCommand(String imageName, int type, String imageSize, String newPath, String imageType, boolean isExtent) throws Exception {
        String sourceImage=getSourceImagePath(imageName,imageType);

        return getCommand(imageName,type,imageSize,sourceImage,newPath,imageType,isExtent);
    }

    private String getCommand(String imageName, int type, String imageSize,String imagePath, String newPath, String imageType, boolean isExtent) throws Exception {
        StringBuilder cmdStr = new StringBuilder();
        String width = imageSize.substring(0, imageSize.indexOf("x"));

        cmdStr.append(" convert ");
        cmdStr.append(" -scale ").append(imageSize.trim());
        if (!(extent || isExtent)) {
            if (Integer.valueOf(width) < 250) cmdStr.append("^ ");
        }
        //去杂质
        if (Integer.valueOf(width) < 250) {
            cmdStr.append(" -strip -define jpeg:preserve-settings ");
            cmdStr.append(" -sharpen 0x1.2 ");
            cmdStr.append(" -quality 100 ");
        } else {
            cmdStr.append(" -strip -define jpeg:preserve-settings ");
            if (0 < quality && quality < 101) {
                cmdStr.append(" -quality ").append(quality);
            }
        }

        if (extent || isExtent) {//新增填充色配置
            cmdStr.append(" -background ").append(extentColor);
            cmdStr.append(" -gravity center ");
            cmdStr.append(" -extent ").append(imageSize);
        }



        cmdStr.append(" ").
                append(imagePath).append(" ").append(newPath);
        return cmdStr.toString();
    }

    /**
     * 获得一级目录名称
     *
     * @param name
     * @return
     */
    private String getFirstPath(String name) {
        if (name == null)
            return null;
        return name.substring(0, 6);
    }

    /**
     * 获得二级目录名称
     *
     * @param name
     * @return
     */
    private String getSecondPath(String name) {
        if (name == null)
            return null;
        return name.substring(6, 7);
    }

    public void convertType(String src, String desc) throws Exception {
        long begin = System.currentTimeMillis();
        log.debug("----GraphicsImage----convertType---------begin");
        GMConnection connection = null;
        try {
            String command = getConvertTpyrCommand(src, desc);
            log.debug("----GraphicsImage----convertType---------command:" + command);
            connection = gMService.getConnection();
            connection.execute(command);
        } catch (Exception e) {
            log.error("", e);
            throw e;
        } finally {
            if (connection != null)
                connection.close();
        }
        log.debug("----GraphicsImage----convertType---------end 耗时" + (System.currentTimeMillis() - begin));
    }

    private String getConvertTpyrCommand(String src, String desc) {
        //去杂质
        return " convert" + " -strip -define jpeg:preserve-settings " + " -quality 100" + " " + src + " " + desc;
    }

    /**
     * 图片加水印命令
     * @param imageName 图片名称
     * @param newPath 新图片的位置
     * @param imageType 图片类型
     * @return 命令
     * @throws Exception
     */
//    private String getCommandOfWatermark(String imageName,String newPath, String imageType) throws Exception {
//        if(!StringUtils.hasText(logoFile)){
//            log.error("图片打水印错误，原因：不存在水印。");
//            return "";
//        }
//        StringBuilder cmdStr = new StringBuilder();
//        cmdStr.append(" composite ").
//                append(logoFile).
//                append(" ").
//                append(imageRoot).append(imageRoot.endsWith(File.separator) ? "" : File.separator).
//                append(getFirstPath(imageName)).append(File.separator).
//                append(getSecondPath(imageName)).append(File.separator).
//                append(imageName).append(imageType).
//                append(" ").
//                append(" -gravity ").
//                append(logoPosition).
//                append(" -geometry +10+10 -quality 100 ").
//                append(newPath);
//        return cmdStr.toString();
//    }

    /**
     * 图片加水印命令
     * @param imageName 图片名称
     * @param newPath 新图片的位置
     * @param imageType 图片类型
     * @return 命令
     * @throws Exception
     */
    private String getCommandOfWatermark(String imageName,String newPath, String imageType) throws Exception {
        if(!StringUtils.hasText(logoFile)){
            log.error("图片打水印错误，原因：不存在水印。");
            return "";
        }
        String nimageId=imageName;
        if(FileUtil.fileSysType==2 && imageName.contains("/")){
            nimageId=nimageId.substring(nimageId.lastIndexOf("/")+1,nimageId.indexOf("."));
        }
        //图片路径
        StringBuilder sb=new StringBuilder(64);
        sb.append(imageRoot).append(imageRoot.endsWith(File.separator) ? "" : File.separator).
                append(getFirstPath(nimageId)).append(File.separator).
                append(getSecondPath(nimageId)).append(File.separator).
                append(nimageId).append(imageType);

        BufferedImage bufferedImage = ImageIO.read(new File(sb.toString()));
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int scaleH=Float.valueOf((width/2)*((logHeight*1.0f)/logWidth)).intValue();
        //居中
        int x=width/4;
        int y=(height-scaleH)/2;
        if("EN".equalsIgnoreCase(logoPosition)){
            //右下角
            x=width/2-5;
            y=height-scaleH-5;
        }



        StringBuilder cmdStr = new StringBuilder();
        cmdStr.append(" convert -draw \"image Over ").
                append(x).
                append(",").
                append(y).
                append(" ").
                append(width/2).
                append(",").
                append(scaleH).
                append(" ").
                append(logoFile).
                append(" \" ").
                append("-quality 100 ").
                append(sb.toString()).
                append(" ").
                append(newPath);

        if(log.isInfoEnabled()){
            log.info("图片加水印命令行："+cmdStr.toString());
        }

        return cmdStr.toString();
    }
}
