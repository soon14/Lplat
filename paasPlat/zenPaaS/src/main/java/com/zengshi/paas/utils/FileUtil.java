/** 
 * Date:2015年8月24日上午11:17:01 
 * 
*/
package com.zengshi.paas.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.zengshi.paas.file.FileManagerSVC;
import com.mongodb.gridfs.GridFSDBFile;

/** 
 * Description: <br>
 * Date:2015年8月24日上午11:17:01  <br>
 * 
 * @since JDK 1.6 
 * @see       
 */
public class FileUtil {

    private static FileManagerSVC fileManager;
    public static int fileSysType=0;//文件存储方式
    static{
        fileManager=PaasContextHolder.getContext().getBean("fileManager",FileManagerSVC.class);

        String fileSys=(String)SystemPropertiesUtil.getPropertiesValue("file_system");
        if("mongodb".equalsIgnoreCase(fileSys)){
            fileSysType=1;
        }else if("fastdfs".equalsIgnoreCase(fileSys)){
            fileSysType=2;
        }else{
            fileSysType=0;
        }
    }
    /**
     * 
     * saveFile:保存文件，返回文件ID. <br/> 
     * 
     * 
     * @param fileName 文件路径
     * @param fileType 文件类型
     * @return 
     * @since JDK 1.6
     */
    public static String saveFile(String fileName, String fileType) {
        if(fileSysType==1){
            return fileManager.saveFile(fileName, fileType);
        }else if(fileSysType==2){
            return FastDFSUtil.uploadFile(fileName,null);
        }else{
            return fileManager.saveFile(fileName, fileType);
        }
    }
    /**
     * 
     * saveFile:保存文件，返回文件ID. <br/> 
     * 
     * 
     * @param byteFile 文件字节数组
     * @param fileName 文件名
     * @param fileType 文件类型
     * @return 
     * @since JDK 1.6
     */
    public static String saveFile(byte[] byteFile, String fileName, String fileType) {
        if(fileSysType==1){
            return fileManager.saveFile(byteFile, fileName, fileType);
        }else if(fileSysType==2){
            return FastDFSUtil.uploadFile(byteFile,fileName,fileType,null);
        }else{
            return fileManager.saveFile(byteFile, fileName, fileType);
        }
    }
    /**
     *
     * saveFile:保存文件，返回文件ID. <br/>
     *
     *
     * @param inputStream 文件流
     * @param fileName 文件名
     * @param fileType 文件类型
     * @return
     * @since JDK 1.6
     */
    public static String saveFile(InputStream inputStream, String fileName, String fileType) {
        if(fileSysType==1){
            return fileManager.saveFile(inputStream, fileName, fileType);
        }else if(fileSysType==2){
            return FastDFSUtil.uploadFile(inputStream,fileName,fileType,null);
        }else{
            return fileManager.saveFile(inputStream, fileName, fileType);
        }
    }
    
    /**
     * @param fileId 文件
     */
    public static String getFileName(String fileId){
//        if(fileSysType==1){
//            return fileManager.getFileName(fileId);
//        }else if(fileSysType==2){
//            return FastDFSUtil.getFileName(fileId);
//        }else{
//            return fileManager.getFileName(fileId);
//        }
        if(fileId.contains("/")){
            return FastDFSUtil.getFileName(fileId);
        }else{
            return fileManager.getFileName(fileId);
        }
    }

    /**
     *获取文件类型
     * @param fileId 文件
     * @return
     */
    public static String getFileType(String fileId){
//        if(fileSysType==1){
//            return fileManager.getFileType(fileId);
//        }else if(fileSysType==2){
//            return FastDFSUtil.getFileType(fileId);
//        }else{
//            return fileManager.getFileType(fileId);
//        }
        if(fileId.contains("/")){
            return FastDFSUtil.getFileType(fileId);
        }else{
            return fileManager.getFileType(fileId);
        }
    }

    /**
     * 获取文件大小
     * @param fileId
     * @return
     */
    public static long getFileSize(String fileId){

        return  fileManager.getFileSize(fileId);
    }
    
    /**
     * 
     * readFile:读取文件. <br/> 
     * 
     * 
     * @param fileId 文件ID
     * @return 文件字节 数组
     * @since JDK 1.6
     */
    public static byte[] readFile(String fileId) {
//        if(fileSysType==1){
//            return fileManager.readFile(fileId);
//        }else if(fileSysType==2){
//            return FastDFSUtil.download(fileId);
//        }else{
//            return fileManager.readFile(fileId);
//        }

        if(fileId.contains("/")){
            return FastDFSUtil.download(fileId);
        }else{
            return fileManager.readFile(fileId);
        }
    }
    /**
     * 
     * readFile2Text:读取文本内容. <br/> 
     * (适合mongodb)
     * @param fileId
     * @return 
     * @since JDK 1.6
     */
    public static String readFile2Text(String fileId,String charsetName){
        charsetName=StringUtils.isEmpty(charsetName)?"UTF-8":charsetName;
        byte[] bytes= readFile(fileId);
        if(null!=bytes){
            try {
                return new String(bytes,charsetName);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        
        return null;
    }
    /**
     * 
     * readFileByName:读取文件. <br/> 
     * (适用mongodb)
     *
     * 
     * @param fileName 文件名
     * @return 文件字节数组
     * @since JDK 1.6
     */
    public static byte[] readFileByName(String fileName) {
        
        return fileManager.readFileByName(fileName);
    }
    /**
     * 
     * readFile:生成本地文件. <br/> 
     * 
     * 
     * @param fileId 文件ID
     * @param localFileName 本地文件名 
     * @since JDK 1.6
     */
    public static void readFile(String fileId, String localFileName) {
//        if(fileSysType==1){
//            fileManager.readFile(fileId, localFileName);
//        }else if(fileSysType==2){
//            FastDFSUtil.download2local(fileId,localFileName);
//        }else{
//            fileManager.readFile(fileId, localFileName);
//        }
        if(fileId.contains("/")){
            FastDFSUtil.download2local(fileId,localFileName);
        }else{
            fileManager.readFile(fileId, localFileName);
        }
    }
    /**
     * 
     * readFileByName:生成本地文件. <br/> 
     * (适用mongodb)
     *
     * 
     * @param fileName 原文件名
     * @param localFileName 本地文件名
     * @since JDK 1.6
     */
    @Deprecated
    public static void readFileByName(String fileName, String localFileName) {
        
        fileManager.readFileByName(fileName, localFileName);
    }
    /**
     * 
     * deleteFile:删除文件. <br/> 
     * 
     * @param fileId 文件ID
     * @since JDK 1.6
     */
    public static void deleteFile(String fileId) {
//        if(fileSysType==1){
//            fileManager.deleteFile(fileId);
//        }else if(fileSysType==2){
//            FastDFSUtil.deleteFile(fileId);
//        }else{
//            fileManager.deleteFile(fileId);
//        }

        if(fileId.contains("/")){
            FastDFSUtil.deleteFile(fileId);
        }else {
            fileManager.deleteFile(fileId);
        }
    }
    /**
     * 
     * deleteFileByName:删除文件. <br/> 
     * (适用mongodb)
     *
     * @param fileName 文件名 
     * @since JDK 1.6
     */
    @Deprecated
    public static void deleteFileByName(String fileName) {
        
        fileManager.deleteFileByName(fileName);
    }

    /**
     * (适用mongodb)
     * @param condition
     * @return
     */
    @Deprecated
    public static List<GridFSDBFile> queryWithCondition(Map<String, String> condition){
        
        return fileManager.queryWithCondition(condition);
    }

    /**
     * (适用mongodb)
     * @param byteFile
     * @param fileId
     * @param fileName
     * @param fileType
     * @return
     * @throws Exception
     */
    @Deprecated
    public static String updateFile(byte[] byteFile, String fileId,String fileName,String fileType) throws Exception{
        
        return fileManager.updateFile(byteFile, fileId, fileName, fileType);
    }
    /**
     * 
     * updatePropertyOfFile:修改fileName或fileType. <br/> 
     * (适用mongodb)
     *
     * @param fileId
     * @param fileName
     * @param fileType
     * @return
     * @throws Exception 
     * @since JDK 1.6
     */
    @Deprecated
    public static String updatePropertyOfFile(String fileId,String fileName,String fileType) throws Exception{
        
        String id=fileManager.updatePropertyOfFile(fileId, fileName, fileType);
        
        return id;
    }

    /**
     * (适用mongodb)
     * @param fileId
     * @param localFileName
     * @return
     * @throws Exception
     */
    @Deprecated
    public static Date readFileAndUpdateTime(String fileId,String localFileName) throws Exception{
        
        return fileManager.readFileAndUpdateTime(fileId, localFileName);
    }

    /**
     * (适用mongodb)
     * @param fileId
     * @return
     * @throws Exception
     */
    @Deprecated
    public static Date readUpdateTime(String fileId) throws Exception{
        
        return fileManager.readUpdateTime(fileId);
    }

    public static void readFile(String fileId, OutputStream ops){
//        if(fileSysType==1){
//            fileManager.readFile(fileId,ops);
//        }else if(fileSysType==2){
//            FastDFSUtil.download2stream(fileId,ops);
//        }else{
//            fileManager.readFile(fileId,ops);
//        }
        if(fileId.contains("/")){
            FastDFSUtil.download2stream(fileId,ops);
        }else{
            fileManager.readFile(fileId,ops);
        }
    }

    public static InputStream readFile2ips(String fileId){
//        if(fileSysType==1){
//            return fileManager.readFile2ips(fileId);
//        }else if(fileSysType==2){
//            return FastDFSUtil.download2stream(fileId);
//        }else{
//            return fileManager.readFile2ips(fileId);
//        }
        if(fileId.contains("/")){
            return FastDFSUtil.download2stream(fileId);
        }else{
            return fileManager.readFile2ips(fileId);
        }
    }

    public static void main(String[] args) {
//        String id=saveFile("G:\\common.sql", "sql");
//        System.out.println("=============id:"+id);
//        updatePropertyOfFile();
        String type=getFileType("5667f8f1c768dd241f1fbbf3");
        System.out.println("++++++++++++++++++++type: "+type);
    }
}

