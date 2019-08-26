package com.zengshi.paas.utils;

import com.zengshi.paas.fastdfs.FastDFSFile;
import org.apache.log4j.Logger;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 */
public class FastDFSUtil {
    private static final Logger logger=Logger.getLogger(FastDFSUtil.class);

    private static TrackerClient trackerClient;
    private static TrackerServer trackerServer;
    private static StorageServer storageServer;
    private static StorageClient storageClient;
    private static StorageClient1 storageClient1;

    static{
        Resource fileRource = new ClassPathResource("fdfs_client.conf");
        try {
            String clientCfg=fileRource.getFile().getPath();
            ClientGlobal.init(clientCfg);
            trackerClient = new TrackerClient();
            trackerServer = trackerClient.getConnection();
            storageClient = new StorageClient(trackerServer, storageServer);
            storageClient1 = new StorageClient1(trackerServer, storageServer);
        } catch (IOException | MyException e) {
            logger.error("fdfs_client.conf文件加载错误",e);
//            throw new RuntimeException("fdfs_client.conf文件加载错误",e);
        }
    }

    public static String upload(FastDFSFile file,String groupName){
        Map<String,String> nameValuePairs=file.getMetaData();
        NameValuePair[] meta_list=null;
        if(null==nameValuePairs && StringUtils.hasText(file.getName())){
            nameValuePairs=new HashMap<String,String>();
            nameValuePairs.put("fileName",file.getName());
            nameValuePairs.put("fileExt",file.getExt());
        }
        if(!CollectionUtils.isEmpty(nameValuePairs)){
            int size=nameValuePairs.size();
            meta_list=new NameValuePair[size];
            Set<Map.Entry<String,String>> entries= nameValuePairs.entrySet();
            int idx=0;
            for(Map.Entry<String,String> entry : entries){
                meta_list[idx++]=new NameValuePair(entry.getKey(),entry.getValue());
            }
        }
        String fileId=null;
        try {
            if(StringUtils.hasText(groupName)){
                fileId=storageClient1.upload_file1(groupName,file.getContent(),file.getExt(),meta_list);
            }else{
                fileId=storageClient1.upload_file1(file.getContent(),file.getExt(),meta_list);
            }
        } catch (IOException | MyException e) {
            throw new RuntimeException("文件["+file.getName()+"]上传失败",e);
        }
        return fileId;
    }

    /**
     * 分布式文件存储
     * @param ips 文件流
     * @param fileName 文件名
     * @param fileExt 文件扩展名
     * @param metaData 文件元数据
     * @param groupName 分卷名
     * @return
     */
    public static String uploadFile(InputStream ips,String fileName,String fileExt,Map<String,String> metaData,String groupName){
        byte[] file_buff = null;
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = ips.read(buffer))) {
                output.write(buffer, 0, n);
            }
            file_buff=output.toByteArray();
        }catch (IOException e){
            throw new RuntimeException("文件流转字节错误。",e);
        }
        FastDFSFile file=new FastDFSFile(fileName,file_buff,fileExt,metaData);
        String fileId=upload(file,groupName);
        return fileId;
    }
    /**
     * 分布式文件存储
     * @param ips 文件流
     * @param fileName 文件名
     * @param fileExt 文件扩展名
     * @param metaData 文件元数据
     * @return
     */
    public static String uploadFile(InputStream ips,String fileName,String fileExt,Map<String,String> metaData){

        return uploadFile(ips,fileName,fileExt,metaData,null);
    }

    /**
     * 分布式文件存储
     * @param filePath 本地文件
     * @param metaData 附带文件元信息
     * @param groupName 分卷名
     * @return
     */
    public static String uploadFile(String filePath,Map<String,String> metaData,String groupName){
        File file=new File(filePath);
        try {
            FileInputStream fis=new FileInputStream(file);
            String fileName=file.getName();
            int idx=fileName.lastIndexOf(".");
            String fileId=uploadFile(fis,fileName.substring(0,idx),fileName.substring(idx+1),metaData,groupName);
            return fileId;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("文件["+filePath+"]没发现。",e);
        }
    }
    /**
     * 分布式文件存储
     * @param filePath 本地文件
     * @param metaData 附带文件元信息
     * @return
     */
    public static String uploadFile(String filePath,Map<String,String> metaData){

        return uploadFile(filePath,metaData,null);
    }

    public static String uploadFile(byte[] byteFile,String fileName,String fileExt,Map<String,String> metaData,String groupName){
        FastDFSFile file=new FastDFSFile(fileName,byteFile,fileExt,metaData);
        String fileId=upload(file,groupName);
        return fileId;
    }

    public static String uploadFile(byte[] byteFile,String fileName,String fileExt,Map<String,String> metaData){
        return uploadFile(byteFile,fileName,fileExt,metaData);
    }

    /**
     * 获取文件信息
     * @param fileId 文件ID
     * @return
     */
    public static FileInfo getFileInfo(String fileId){
        try {
            return storageClient1.get_file_info1(fileId);
        } catch (IOException | MyException e) {
            throw new RuntimeException("获取文件["+fileId+"]信息错误",e);
        }
    }

    public static String getFileName(String fileId){
        Map<String,String> metaData=getMetaData(fileId);
        if(null==metaData){
            return null;
        }
        return metaData.get("fileName");
    }

    public static String getFileType(String fileId){
        Map<String,String> metaData=getMetaData(fileId);
        if(metaData==null){
            return null;
        }
        return metaData.get("fileExt");
    }

    /**
     * 获取文件元数据
     * @param fileId
     * @return
     */
    public static Map<String,String> getMetaData(String fileId){

        try {
            NameValuePair[] nameValuePairs=storageClient1.get_metadata1(fileId);
            if(null==nameValuePairs){
                return null;
            }
            Map<String,String> meta=new HashMap<String,String>();
            for(NameValuePair nvp : nameValuePairs){
                meta.put(nvp.getName(),nvp.getValue());
            }
            return meta;
        } catch (IOException | MyException e) {
            throw new RuntimeException("获取文件["+fileId+"]元数据错误。",e);
        }
    }

    /**
     * 下载文件
     * @param fileId 文件id
     * @return 字节数组
     */
    public static byte[] download(String fileId){
        try {
            return storageClient1.download_file1(fileId);
        } catch (IOException |MyException e) {
            throw new RuntimeException("文件["+fileId+"]下载失败",e);
        }
    }

    /**
     * 下载文件
     * @param fileId 文件id
     * @return 输入流
     */
    public static InputStream  download2stream(String fileId){
        byte[] bytes=download(fileId);
        return new ByteArrayInputStream(bytes);
    }

    /**
     * 下载文件
     * @param fileId
     * @param ops
     */
    public static void download2stream(String fileId,OutputStream ops){
        try{
            InputStream ips=download2stream(fileId);
            int ch;
            while ((ch = ips.read()) != -1) {
                ops.write(ch);
            }
        }catch (Exception e){
            throw new RuntimeException("文件转换成输出流错误。",e);
        }

    }

    /**
     * 下载文件
     * @param fileId 文件名
     * @param localFileName 本地文件名
     * @return
     */
    public static int download2local(String fileId,String localFileName){
        try {
            return storageClient1.download_file1(fileId,localFileName);
        } catch (IOException | MyException e) {
            throw new RuntimeException("文件["+fileId+"]下载错误。",e);
        }
    }

    /**
     * 下载文件
     * @param fileId 文件ID
     * @param cb 回调对象（针对大文件分段读取）
     * @return
     */
    public static int download(String fileId,DownloadCallback cb){

        try {
            return storageClient1.download_file1(fileId,cb);
        } catch (IOException | MyException e) {
            throw new RuntimeException("文件["+fileId+"]下载错误。",e);
        }
    }

    /**
     * 下载文件
     * @param fileId 文件ID
     * @param file_offset 偏移量
     * @param download_bytes 下载长度
     * @return
     */
    public static byte[] download(String fileId,long file_offset, long download_bytes){

        try {
            return storageClient1.download_file1(fileId,file_offset,download_bytes);
        } catch (IOException | MyException e) {
            throw  new RuntimeException("下载文件["+fileId+"]错误。",e);
        }
    }

    /**
     * 删除文件
     * @param fileId 文件ID
     * @return
     */
    public static int deleteFile(String fileId){

        try {
            return storageClient1.delete_file1(fileId);
        } catch (IOException | MyException e) {
            throw new RuntimeException("删除文件["+fileId+"]失败",e);
        }
    }

    public static String upload4salve(String masterFileId,String prefixName,InputStream ips,String fileExtName,Map<String,String> metaData){
        byte[] file_buff = null;
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = ips.read(buffer))) {
                output.write(buffer, 0, n);
            }
            file_buff=output.toByteArray();
        }catch (IOException e){
            throw new RuntimeException("文件流转字节错误。",e);
        }
        return upload4salve(masterFileId,prefixName,file_buff,fileExtName,metaData);
    }

    public static String upload4salve(String masterFileId,String prefixName, byte[] fileBuff, String fileExtName,Map<String,String> metaData){
        NameValuePair[] meta_list=null;
        if(!CollectionUtils.isEmpty(metaData)){
            int size=metaData.size();
            meta_list=new NameValuePair[size];
            Set<Map.Entry<String,String>> entries= metaData.entrySet();
            int idx=0;
            for(Map.Entry<String,String> entry : entries){
                meta_list[idx++]=new NameValuePair(entry.getKey(),entry.getValue());
            }
        }
        try {
            return storageClient1.upload_file1(masterFileId,prefixName,fileBuff,fileExtName,meta_list);
        } catch (IOException | MyException e) {
            throw new RuntimeException("从文件上传失败。",e);
        }
    }

    public static String upload4salve(String masterFileId,String prefixName, String localFileName, Map<String,String> metaData){

        try {
            File file=new File(localFileName);
            String fileName=file.getName();
            int idx=fileName.lastIndexOf(".");
            FileInputStream fis=new FileInputStream(file);
            return upload4salve(masterFileId,prefixName,fis,fileName.substring(idx+1),metaData);
        } catch (IOException e) {
            throw new RuntimeException("从文件上传失败。",e);
        }
    }

    public static StorageClient getStorageClient(){
        return storageClient;
    }

    public static StorageClient1 getStorageClient1(){
        return storageClient1;
    }

    public static TrackerClient getTrackerClient(){
        return trackerClient;
    }


    public static void main(String[] args) throws IOException {
//        String fileId2=uploadFile("E:/sy.png",null);
//        System.out.println(fileId2);

        FileInfo fileInfo=getFileInfo("group1/M00/00/00/wKgBZlimpQCAeANSAAB8yanPCHU249.png");
        System.out.println(fileInfo);
//        Map<String,String> metaData=getMetaData("group1/M00/00/00/wKgBZlimpQCAeANSAAB8yanPCHU249.png");

//        int i=download2local("group1/M00/00/00/wKgBZlimpQCAeANSAAB8yanPCHU249.png","E:/01.png");
//        int i=deleteFile("group1/M00/00/00/wKgBZlimpQCAeANSAAB8yanPCHU249.png.png");

//        String fileId=upload4salve(fileId2,"_400x400","E:/sy.png","png",null);

//        System.out.println(fileId);

//        String fileName=getFileName("group1/M00/00/00/wKgBZlimpQCAeANSAAB8yanPCHU249.png");
//        System.out.println(fileName);

        System.out.println("group1/M00/00/00/wKgBZlimpQCAeANSAAB8yanPCHU249.png".length());
    }
}
