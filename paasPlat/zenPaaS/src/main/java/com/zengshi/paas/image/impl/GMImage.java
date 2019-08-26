package com.zengshi.paas.image.impl;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.zengshi.paas.ConfigurationCenter;
import com.zengshi.paas.ConfigurationWatcher;
import com.zengshi.paas.PaasException;
import com.zengshi.paas.image.IImage;
import com.zengshi.paas.utils.JSONValidator;

import net.sf.json.JSONObject;

public class GMImage implements ConfigurationWatcher,IImage {
	
	private static final Logger log = Logger.getLogger(GMImage.class);
	private String confPath = "";
	
	private static final String GM_PATH_KEY = "gmPath";
	private static final String MAX_ACTIVE_KEY = "maxActive";
	private static final String MAX_WAIT_KEY = "maxWait";
	private static final String MAX_IDLE_KEY = "maxIdle";
	private static final String TEST_ON_BORROW_KEY = "testOnBorrow";
	private static final String TEST_ON_RETURN_KEY = "testOnReturn";
	private static final String IMAGE_ROOT = "imageRoot";
	private static final String IMAGE_ROOT_NEW = "imageRootNew";
	private static final String IMAGE_NAME_SPLIT = "imageNameSplit"; 
	private static final String IMAGE_TYPE = "imageType"; 
	private static final String GM_MODE_KEY = "gmMode";
	private static final String RESERVE_IMAGE_KEY = "reserveImage";
	private static final String EXTENT_KEY = "extent";
	private static final String EXTENT_COLOR_KEY = "extentColor";
	private static final String QUALITY_KEY = "quality"; 
	
	private static final String REDISKEYPREFIX_KEY = "redisKeyPrefix"; 
	private static final String VARNISHSERVER_KEY = "varnishServer"; 
	private static final String COMMANDSIZE_KEY = "commandSize"; 
	private static final String PRIMARYPARAM_KEY = "primaryParam"; 
	private static final String UPPATH_KEY = "upPath"; 
	private static final String UPPATHNEW_KEY = "upPathNew"; 
	
	private static final String SERVERURL_KEY = "serverUrl"; 
	private static final String UPLOADURL_KEY = "uploadUrl"; 
	
	private static final String DEFAULT_IMAGE= "defaultImage";

	private static final String LOGO_FILE_KEY="logoFile";//logo水印图片文件
	private static final String IMAGE_MARK_ROOT_NEW="imageMarkRootNew";//打水印文件路径
	private static final String LOGO_POSITION_KEY="logoPosition";//水印位置 NorthWest, North, NorthEast, West, Center, East, SouthWest, South, SouthEast


	private String  gmPath = null;
	
	//本地保存图片的路径 源图
	private String imageRoot = null;
	//图片名分隔符 _
	private String imageNameSplit = null;
	//图片格式 .jpg等
	private String imageType = null;
	//本地保存图片的路径 缩略图
	private String imageRootNew = null;
	//本地保存打水印图片的路径 缩略图
	private String imageMarkRootNew = null;
	//水印位置
	private String logoPosition="Center";
	//水印logo文件
	private String logoFile;
	//是否补白尺寸图
	private boolean extent = true;
	//补白颜色
	private String extentColor="white";
	//图片质量
	private int quality = 90; 
		
	private int maxActive;
	private long maxWait;
	private int maxIdle;
	private boolean testOnBorrow;
	private boolean testOnReturn;
	
	private String redisKeyPrefix;
	private String varnishServer;
	//与varnish一次连接，清除缓存url的个数
	private int commandSize;
	private String primaryParam;
	//是否开启graphicsmagick模式
	private boolean gmMode = true;
	//异常时，返回异常图片的路径
	private String reserveImage = null;
	private List<String> types = null;
	//上传图片  本地存放路径
	private String upPath;
	//上传图片  转换格式后的本地路径
	private String upPathNew;
	//图片服务器地址
	private String serverUrl;
	//多图片服务器地址
	private String[] serverUrls;
	//图片上传，使用内网地址
	private String uploadUrl;
	//默认图片，在没有图片。或者获取的图片错误的时候，进行的展示；
	private String defaultImage = "";
	
	private GMClient gMClient;
	private ConfigurationCenter confCenter = null;

	public void init() {
		try {
			process(confCenter.getConfAndWatch(confPath, this));
		} catch (PaasException e) {
			log.error("", e);
		}
	}

	@Override
	public boolean localImageExist(String localPath,String imageType) throws Exception {
		return gMClient.localImageExist(localPath,imageType);
	}

	@Override
	public void getRomteImage(String imageId,String imageType) throws Exception {
		gMClient.getRomteImage(imageId, imageType);
	}

	@Override
	public String scaleImage(String uri, String imageName, int type,
			String imageSize, String imageType,boolean isExtent) throws Exception {
		return gMClient.scaleImage(uri, imageName, type, imageSize, imageType,isExtent);
	}

	@Override
	public String scaleWatermarkImage(String uri, String imageName, int type, String imageSize, String imageType, boolean isExtent) throws Exception {

		return gMClient.scaleWatermarkImage(uri, imageName, imageSize, imageType,isExtent);
	}

	@Override
	public void addImgText(String srcPath, String newPath) throws Exception {
		gMClient.addImgText(srcPath, newPath);
	}

	@Override
	public void saveCacheImage(String imageName, String imageSize,
			String newPath,String imageType) throws Exception {
		gMClient.saveCacheImage(imageName, imageSize, newPath, imageType);
	}

	@Override
	public void removeLocalCutedImage(String path) throws Exception {
		gMClient.removeLocalCutedImage(path);
	}

	@Override
	public String saveToRomte(File image,String imageName, String imageType) throws Exception {
		return gMClient.saveToRomte(image,imageName,imageType);
	}

	@Override
	public void process(String conf) {
		if (log.isInfoEnabled()) {
			log.info("new log configuration is received: " + conf);
		}
		JSONObject jsonObj = JSONObject.fromObject(conf);
		boolean changed = false;
		changed = initConfAndChangeed(jsonObj,changed);
		if (changed) {
			gMClient = new GMClient(conf);
			if (log.isInfoEnabled()) {
				log.info("gm server address is changed to " + conf);
			}
		}
	}
	
	

	private boolean initConfAndChangeed(JSONObject jsonObj, boolean changed) {
		if (JSONValidator.isChanged(jsonObj, GM_PATH_KEY, gmPath)) {
			changed = true;
			gmPath = jsonObj.getString(GM_PATH_KEY);
		}
		if (JSONValidator.isChanged(jsonObj, MAX_ACTIVE_KEY, maxActive+"")) {
			changed = true;
			maxActive = jsonObj.getInt(MAX_ACTIVE_KEY);
		}
		if (JSONValidator.isChanged(jsonObj, MAX_WAIT_KEY, maxWait+"")) {
			changed = true;
			maxWait = jsonObj.getLong(MAX_WAIT_KEY);
		}
		if (JSONValidator.isChanged(jsonObj, MAX_IDLE_KEY, maxIdle+"")) {
			changed = true;
			maxIdle = jsonObj.getInt(MAX_IDLE_KEY);
		}
		if (JSONValidator.isChanged(jsonObj, TEST_ON_BORROW_KEY, testOnBorrow+"")) {
			changed = true;
			testOnBorrow = jsonObj.getBoolean(TEST_ON_BORROW_KEY);
		}
		if (JSONValidator.isChanged(jsonObj, TEST_ON_RETURN_KEY, testOnReturn+"")) {
			changed = true;
			testOnReturn = jsonObj.getBoolean(TEST_ON_RETURN_KEY);
		}
		if (JSONValidator.isChanged(jsonObj, IMAGE_ROOT, imageRoot)) {
			changed = true;
			imageRoot = jsonObj.getString(IMAGE_ROOT);
		}
		if (JSONValidator.isChanged(jsonObj, IMAGE_ROOT_NEW, imageRootNew)) {
			changed = true;
			imageRootNew = jsonObj.getString(IMAGE_ROOT_NEW);
		}
		if (JSONValidator.isChanged(jsonObj, IMAGE_NAME_SPLIT, imageNameSplit)) {
			changed = true;
			imageNameSplit = jsonObj.getString(IMAGE_NAME_SPLIT);
		}
		if (JSONValidator.isChanged(jsonObj, IMAGE_TYPE, imageType)) {
			changed = true;
			imageType = jsonObj.getString(IMAGE_TYPE);
			if(imageType==null||"".equals(imageType)){
				types = Arrays.asList(new String[]{".jpg"});
			}else{
				types = Arrays.asList(imageType.split(","));
			}
		}
		if (JSONValidator.isChanged(jsonObj, EXTENT_KEY, extent+"")) {
			changed = true;
			extent = jsonObj.getBoolean(EXTENT_KEY);
		}
		if(JSONValidator.isChanged(jsonObj,EXTENT_COLOR_KEY,extentColor)){
			changed=true;
			extentColor=jsonObj.getString(EXTENT_COLOR_KEY);
		}
		if (JSONValidator.isChanged(jsonObj, QUALITY_KEY, quality+"")) {
			changed = true;
			quality = jsonObj.getInt(QUALITY_KEY);
		}
		
		if (JSONValidator.isChanged(jsonObj, GM_MODE_KEY, gmMode+"")) {
			gmMode = jsonObj.getBoolean(GM_MODE_KEY);
		}
		if (JSONValidator.isChanged(jsonObj, RESERVE_IMAGE_KEY, reserveImage)) {
			reserveImage = jsonObj.getString(RESERVE_IMAGE_KEY);
		}
		if (JSONValidator.isChanged(jsonObj, REDISKEYPREFIX_KEY, redisKeyPrefix)) {
			changed = true;
			redisKeyPrefix = jsonObj.getString(REDISKEYPREFIX_KEY);
		}
		if (JSONValidator.isChanged(jsonObj, VARNISHSERVER_KEY, varnishServer)) {
			changed = true;
			varnishServer = jsonObj.getString(VARNISHSERVER_KEY);
		}
		if (JSONValidator.isChanged(jsonObj, COMMANDSIZE_KEY, commandSize+"")) {
			changed = true;
			commandSize = jsonObj.getInt(COMMANDSIZE_KEY);
		}
		if (JSONValidator.isChanged(jsonObj, UPPATH_KEY, upPath)) {
			upPath = jsonObj.getString(UPPATH_KEY);
		}
		if (JSONValidator.isChanged(jsonObj, UPPATHNEW_KEY, upPathNew)) {
			upPathNew = jsonObj.getString(UPPATHNEW_KEY);
		}
		if (JSONValidator.isChanged(jsonObj, PRIMARYPARAM_KEY, primaryParam)) {
			changed = true;
			primaryParam = jsonObj.getString(PRIMARYPARAM_KEY);
		}
		
		if (JSONValidator.isChanged(jsonObj, SERVERURL_KEY, serverUrl)) {
			serverUrl = jsonObj.getString(SERVERURL_KEY);
			if(null!=serverUrl){//jys 2017-01-22 多图片服务器
				serverUrls=serverUrl.split(",");
				if(serverUrls!=null && serverUrls.length>0){
					serverUrl=serverUrls[0];
				}
			}
		}
		if (JSONValidator.isChanged(jsonObj, UPLOADURL_KEY, uploadUrl)) {
			uploadUrl = jsonObj.getString(UPLOADURL_KEY);
		}
		if (JSONValidator.isChanged(jsonObj, IMAGE_MARK_ROOT_NEW, imageMarkRootNew)) {
			imageMarkRootNew = jsonObj.getString(IMAGE_MARK_ROOT_NEW);
		}
		if (JSONValidator.isChanged(jsonObj, LOGO_FILE_KEY, logoFile)) {
			changed = true;
			logoFile = jsonObj.getString(LOGO_FILE_KEY);
		}
		if (JSONValidator.isChanged(jsonObj, LOGO_POSITION_KEY, logoPosition)) {
			changed = true;
			logoPosition = jsonObj.getString(LOGO_POSITION_KEY);
		}
		
		//增加默认图片ID;
		if(jsonObj.containsKey(DEFAULT_IMAGE) && JSONValidator.isChanged(jsonObj, DEFAULT_IMAGE, defaultImage)){
		    defaultImage = jsonObj.getString(DEFAULT_IMAGE);
		}
		return changed;
	}

	public String getConfPath() {
		return confPath;
	}

	public void setConfPath(String confPath) {
		this.confPath = confPath;
	}

	public ConfigurationCenter getConfCenter() {
		return confCenter;
	}

	public void setConfCenter(ConfigurationCenter confCenter) {
		this.confCenter = confCenter;
	}

	@Override
	public String getSourceImagePath(String imageName,String imageType) throws Exception {
		return gMClient.getSourceImagePath(imageName, imageType);
	}

	@Override
	public boolean getGMMode() {
		return gmMode;
	}

	@Override
	public String getReservePath() {
		return reserveImage;
	}

	@Override
	public boolean supportService(String imageTypeReq) {
		return types.contains(imageTypeReq);
	}

	@Override
	public boolean removeCacheHtml(String productId) throws Exception {
		return gMClient.removeCacheHtml(productId);
	}

	@Override
	public void convertType(String srcImage, String descImage) throws Exception {
		String src = upPath.endsWith(File.separator)?(upPath+srcImage):(upPath+File.separator+srcImage);
		String desc = upPathNew.endsWith(File.separator)?(upPathNew+descImage):(upPathNew+File.separator+descImage);
		gMClient.convertType(src,desc);
	}

	@Override
	public String getUpPath(){
		return upPath;
	}

	@Override
	public String getUpPathNew(){
		return upPathNew;
	}

	@Override
	public String getSupportType(){
		if(types!=null&&types.size()>0)
			return types.get(0);
		else
			return imageType;
	}

	@Override
	public String getImageServer() {
		if(serverUrls!=null){//jys 2017-01-22 多图片服务器随机选择
			int size=serverUrls.length;
			if(size>1){
				int index=Double.valueOf(Math.floor(Math.random()*size)).intValue();
				return serverUrls[index];
			}
		}
		return serverUrl;
	}

	@Override
	public String getImageUploadUrl() {
		return uploadUrl;
	}

    public String getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(String defaultImage) {
        this.defaultImage = defaultImage;
    }
	
	

	
}
