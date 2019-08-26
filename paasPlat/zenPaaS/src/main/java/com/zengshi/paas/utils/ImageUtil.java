package com.zengshi.paas.utils;

import java.io.File;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.zengshi.paas.image.IImage;

public class ImageUtil {
    private static final Logger log = Logger.getLogger(ImageUtil.class);

    public static final String no_mark="bba04f1f3972a8b1";//no_mark 字符串加密

    private static IImage imageSv;
    
    static{
        imageSv = (IImage) PaasContextHolder.getContext().getBean("imageSv");
    }

//    private static IImage getIntance() {
//        if (null != imageSv)
//            return imageSv;
//        else {
//            imageSv = (IImage) PaasContextHolder.getContext().getBean("imageSv");
//            return imageSv;
//        }
//    }


    /**
     * 1、本地原图是否存在
     * 2、存在时，生成缩略图；不存在，从mongodb获得原图保存在本地，并生成缩略图
     *
     * @param uri       请求串
     * @param imageName 缩放后图片的路径图片名称
//     * @param type      1为比例处理，2为大小处理，如（比例：1024x1024,大小：50%x50%）
     * @param imageSize 缩放后的图片尺寸 如（比例：1024x1024,大小：50%x50%）
     * @param imageType 图片类型
     * @param isExtent  缩略图是否填充空白
     * @throws Exception
     */
    public static String getScaleImage(String uri, String imageName, String imageSize, String imageType, boolean isExtent) throws Exception {
        long begin = System.currentTimeMillis();
        //处理图片路径
        boolean localExist = imageSv.localImageExist(imageName, imageType);
        log.debug(uri + "--------localExist---------:" + localExist + " 耗时" + (System.currentTimeMillis() - begin));

        if (!localExist) {
            begin = System.currentTimeMillis();
            imageSv.getRomteImage(imageName, imageType);
            log.debug(uri + "--------getRomteImage---------" + " 耗时" + (System.currentTimeMillis() - begin));
        }
        String path = null;
        if (imageSize == null) {
            //源图
            path = imageSv.getSourceImagePath(imageName, imageType);
        } else {
            path = imageSv.scaleImage(uri, imageName, 1, imageSize, imageType, isExtent);
        }
        log.debug(uri + "--------getScaleImage---------:" + localExist + " 耗时" + (System.currentTimeMillis() - begin));
        return path;

    }

    /**
     * 裁剪水印图片
     * @param uri  请求串
     * @param imageName 缩放后图片的路径图片名称
     * @param imageSize 缩放后的图片尺寸 如（比例：1024x1024,大小：50%x50%）
     * @param imageType 图片类型
     * @param isExtent  缩略图是否填充空白
     * @throws Exception
     */
    public static String getScaleWatermarkImage(String uri, String imageName, String imageSize, String imageType, boolean isExtent) throws Exception {
        long begin = System.currentTimeMillis();
        //处理图片路径
        boolean localExist = imageSv.localImageExist(imageName, imageType);
        log.debug(uri + "--------localExist---------:" + localExist + " 耗时" + (System.currentTimeMillis() - begin));

        if (!localExist) {
            begin = System.currentTimeMillis();
            imageSv.getRomteImage(imageName, imageType);
            log.debug(uri + "--------getRomteImage---------" + " 耗时" + (System.currentTimeMillis() - begin));

        }
        String path = imageSv.scaleWatermarkImage(uri, imageName, 1, imageSize, imageType,isExtent);;
        log.debug(uri + "--------getScaleImage---------:" + localExist + " 耗时" + (System.currentTimeMillis() - begin));
        return path;

    }

    /**
     * 是否使用GM模式
     *
     * @return
     */
    public static boolean getGMMode() {
        return imageSv.getGMMode();
    }

    /**
     * 异常时， 图片路径
     *
     * @return
     */
    public static String getReservePath() {
        return imageSv.getReservePath();
    }

    public static boolean supportService(String imageType) {
        return imageSv.supportService(imageType);
    }

    /**
     * 保存文件到mongodb（建议图片存储不要使用该方法）
     *
//     * @param byteFile  图片文件或普通文件
     * @param imageName 图片名称  建议使用商品标识
     * @param imageType 图片格式 例如doc，zip
     * @throws Exception
     */
    public static String saveToRomte(File image, String imageName, String imageType) throws Exception {
        return imageSv.saveToRomte(image, imageName, imageType);
    }

    /**
     * 清除相关产品的静态页缓存（varnish、redis）
     *
     * @param productId
     * @return
     * @throws Exception
     */
    public static boolean removeCacheHtml(final String productId) {
        try {
            return imageSv.removeCacheHtml(productId);
        } catch (Exception e) {
            log.error("", e);
            return false;
        }
    }


    /**
     * 上传图片时，转换图片格式
     *
     * @param srcImage  不带路径
     * @param descImage
     * @throws Exception
     */
    public static void convertType(String srcImage, String descImage) throws Exception {
        imageSv.convertType(srcImage, descImage);
    }

    /**
     * 上传图片  本地存放路径
     *
     * @return
     * @throws Exception
     */
    public static String getUpPath() {
        return imageSv.getUpPath();
    }

    /**
     * 上传图片  转换格式后的本地路径
     *
     * @return
     * @throws Exception
     */
    public static String getUpPathNew() {
        return imageSv.getUpPathNew();
    }

    /**
     * 支持的图片格式类型
     *
     * @return
     * @throws Exception
     */
    public static String getSupportType() {
        return imageSv.getSupportType();
    }

    /**
     * 保存文件到mongodb（建议图片存储不要使用该方法）
     *
     * @param byteFile  图片文件或普通文件
     * @param imageName 图片名称  建议使用商品标识
     * @param imageType 图片格式 例如doc，zip
     * @throws Exception
     */
    public static String saveToRomte(byte[] byteFile, String imageName, String imageType) throws Exception {
        return FileUtil.saveFile(byteFile, imageName, imageType);
    }

    /**
     * 删除mongoDB中的图片
     *
     * @param id
     */
    public static void deleteImage(String id) {
        FileUtil.deleteFile(id);
    }

    /**
     * 图片上传至图片服务器
     * 支持上传的图片格式jpg,jpeg,png,bmp,gif,tiff,raw,pcx,tga,fpx  图片服务器会统一处理成jpg格式。
     *
     * @param image byte[]图片字节数组
     * @param name  图片名称 如：1.jpg  不要带路径
     * @return
     */
    public static String upLoadImage(byte[] image, String name) {
        if (image == null)
            return null;
        if (image.length > 5 * 1024 * 1024) {
            log.error("上传的图片大于5M!");
            return null;
        }
        String id = null;
        String upUrl = getUpImageUrl();
        if (upUrl == null || upUrl.length() == 0) {
            log.error("请检查配置,上传图片的服务器地址。");
            return null;
        }
        String result = HttpUtil.upImage(getUpImageUrl(), image, name);
        JSONObject json = JSONObject.fromObject(result);
        if ("success".equals(json.getString("result"))) {
            id = json.getString("id");
        }
        return id;
    }

    /**
     * 获取图片服务器地址
     *
     * @return
     */
    public static String getImageServer() {
        return imageSv.getImageServer();
    }

    /**
     * 获取图片服务器地址--内网url
     *
     * @return
     */
    public static String getImageServerForIn() {
        return imageSv.getImageUploadUrl();
    }

    /**
     * 获取调用图片的url
     * 
     * 如果，传入的imageId是空，那么获取默认图片；
     * 否则：以"_"分隔，如果第一部分是空或者是"null"，则用默认图片代替，并且保留后面的size部分；
     *
     * @param imageId 图片id 例如53b0dfaf300418069bc01193，53b0dfaf300418069bc01193_100x100
     * @return
     */
    public static String getImageUrl(String imageId) {

        return getImageUrl(imageId,false);
    }

    /**
     *
     * @param imageId 图片ID
     * @param mark 是否加水印
     * @return
     */
    public static String getImageUrl(String imageId,boolean mark){
        StringBuffer strBuf = new StringBuffer();
        boolean isnull=false;//
        if(StringUtils.isEmpty(imageId)){
            strBuf.append(getDefaultImageId());
            isnull=true;
        } else {
            String[] ids = imageId.split("_");
            //如果第一部分是 空，或者是null, 则使用第一部分使用默认图片代替；
            if(StringUtils.isEmpty(ids[0])||"null".equalsIgnoreCase(ids[0])){
                String defId=getDefaultImageId();
                boolean flag=defId.contains(".");//jiangys 2017-02-21 FastDFS
                if(flag){
                    defId=defId.substring(0,defId.lastIndexOf("."));
                }
                strBuf.append(defId);

                if(ids.length>1){
                    strBuf.append("_").append(ids[1]);
                }
                if(flag){//jiangys 2017-02-21 FastDFS
                    String suffix=getDefaultImageId().substring(getDefaultImageId().lastIndexOf("."));
                    strBuf.append(suffix);
                }
                isnull=true;
            } else {
                //保留原样
                strBuf.append(imageId);
            }
        }
 
        boolean fastFlag=!isnull && imageId.contains("/");
        String sec=no_mark;
        String pp=strBuf.toString();
        if(!isnull && mark){
            if(fastFlag){
                int lidx=pp.lastIndexOf("/");
                sec=imageId.substring(lidx+1,lidx+7);
                pp=pp.substring(0,lidx+1)+pp.substring(lidx+7);
            }else{
                sec=pp.substring(0,6);
                pp=pp.substring(6);
            }
            sec=CipherUtil.encrypt(sec+"_mark");
        }
        if(!isnull && imageId.contains(".")){
            return getImageServer() + "/image/"+sec+"/"+ pp;
        }else{
        	String fileType="";
        	fileType=getSupportType();
        	if(!isnull){
        		String objectId=imageId;
        		if(objectId.indexOf("_")!=-1){
        			objectId=objectId.substring(0, objectId.indexOf("_"));
        		}
        		try {
        			fileType=FileUtil.getFileType(objectId);
				} catch (Exception e) {
					// TODO: handle exception
					//取不到或报错就用默认的
				}
        		if(StringUtil.isBlank(fileType)){
        			fileType=getSupportType();
        		}else{
        			if(fileType.indexOf(".")==-1){
        				fileType="."+fileType;
        			}
        		}
        	}
            return getImageServer() + "/image/"+sec+"/" + pp + fileType;
        }
    }

    /**
     * 解析加密的imageId
     * @param imageName
     * @return
     */
    public static String parseImageId(String imageName){
        if(StringUtils.isBlank(imageName)){
            return imageName;
        }
        String name=imageName;
        boolean flag=imageName.contains("/");
        if(flag){
            int fidx=imageName.indexOf("/");//第一个斜杠位置
            name=imageName.substring(0,fidx);//第一个斜杠前字符串
            String sname=imageName.substring(fidx+1);//第一个斜杠后字符串
            if(!no_mark.equals(name)){
                if(sname!=null){
                    String decr=CipherUtil.decrypt(name);
                    String[] decrs=decr.split("_");
                    String dname=decrs[0];
                    if(sname.contains("/")){
                        int sidx=sname.lastIndexOf("/");//最后一个斜杠位置
                        String pname=sname.substring(sidx+1);//最后一个斜杠后字符串
                        name=sname.substring(0,sidx+1)+dname+pname;
                    }else{
                        name=dname+sname;
                    }
                }
            }else{
                name=sname;
            }

        }
        return name;
    }


    public static boolean isMark(String imageName){
        if(StringUtils.isBlank(imageName)){
            return false;
        }
        String name=imageName;
        boolean ismark=false;
        boolean flag=imageName.contains("/");
        if(flag){
            int fidx=imageName.indexOf("/");//第一个斜杠位置
            name=imageName.substring(0,fidx);//第一个斜杠前字符串
            if(!no_mark.equals(name)){
                String sname=imageName.substring(fidx+1);//第一个斜杠后字符串
                if(sname!=null){
                    String decr=CipherUtil.decrypt(name);
                    String[] decrs=decr.split("_");
                    if(decrs.length==2 && "mark".equals(decrs[1])){
                        ismark=true;
                    }
                }

            }
        }
        return ismark;
    }

    /**
     * 获取调用文本、附件的url
     *
     * @param id   文本、附件的id 例如：53b6a32d09a9797981973489
     * @param type 文本、附件的浏览类型，页面直接显示还是下载：doc是下载，html直接显示，为空直接显示
     * @return
     */
    public static String getStaticDocUrl(String id, String type) {
        return getImageServer() + "/static/" + ((StringUtils.isEmpty(type) || "html".equals(type)) ? "html/" : "attch/") + id;
    }

    /**
     * 获取商品缓存页面的url
     *
     * @param id   商品ID
//     * @param type 文本、附件的浏览类型，页面直接显示还是下载：doc是下载，html直接显示，为空直接显示
     * @return
     */
    public static String getRemoveStaticUrl(String id) {
        return getImageServer() + "/removestatic/cacheService/removeCacheHtml/" + id;
    }

    /**
     * 获取上传图片的url
     *
     * @return
     */
    public static String getUpImageUrl() {
        return getImageServerForIn() + "/upImage";
    }
    
    /**
     * 获取配置的默认图片ID; 
     * 
     * @return 
     * @since JDK 1.6
     */
    public static String getDefaultImageId(){
        return imageSv.getDefaultImage();
    }

    public static void main(String[] args) {
        String imageId_nosize = "53b0dfaf300418069bc01193";
        imageId_nosize = "59670fb0b2e61cb3b6068aea";
        String imageId_all = "53b0dfaf300418069bc01193_100x100";
        String imageId_null = "";
        String imageId_onlyId = "53b0dfaf300418069bc01193";
        String imageId_noId = "_100x100";
        String imageId_nofirst = "100x100";
        
        imageId_all=imageId_all.substring(0, imageId_all.indexOf("_"));
        System.out.println(imageId_all);
        System.out.println(ImageUtil.getImageUrl(""));
//        System.out.println(ImageUtil.getImageUrl(imageId_all));
//        System.out.println(ImageUtil.getImageUrl(imageId_null));
//        System.out.println(ImageUtil.getImageUrl(imageId_onlyId));
//        System.out.println(ImageUtil.getImageUrl(imageId_noId));
//        System.out.println(ImageUtil.getImageUrl(imageId_nofirst));
       /* String fileStr = "F:/123.jpg";
        File file = new File(fileStr);
        InputStream input = null;
        try {
            input = new FileInputStream(file);
            byte[] bytes = new byte[input.available()];
            input.read(bytes);
            System.out.println(upLoadImage(bytes, "dian.jpg"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

        }*/
    }

}
