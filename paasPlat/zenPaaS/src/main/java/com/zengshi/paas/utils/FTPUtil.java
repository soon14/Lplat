package com.zengshi.paas.utils;

import com.zengshi.paas.ftp.FTPManagerSVC;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 */
public class FTPUtil {
    private static FTPManagerSVC ftpManager;
    static {
        ftpManager=PaasContextHolder.getContext().getBean("ftpManager",FTPManagerSVC.class);
    }

    /**
     * 断开连接
     */
    public  static void disconnect(){
        ftpManager.disconnect();
    }
    /**
     * 注销
     */
    public static void logout(){
        ftpManager.logout();
    }
    /**
     * 上传文件
     * @param src 源文件
     * @param dst 目标文件
     */
    public static void uploadFile(String src,String dst){
        ftpManager.uploadFile(src,dst);
    }
    /**
     * 上传文件
     * @param src 源文件流
     * @param dst 目标文件
     */
    public static void uploadFile(InputStream src, String dst){
        ftpManager.uploadFile(src,dst);
    }
    /**
     * 以文件块的方式上传文件
     * @param src 源文件
     * @param dst 目标文件
     */
    public static void uploadFileByBlock(String src,String dst){
        ftpManager.uploadFileByBlock(src,dst);
    }
    /**
     * 以块的方式上传文件
     * @param src 源文件流
     * @param dst 目标文件
     */
    public static void uploadFileByBlock(InputStream src,String dst){
        ftpManager.uploadFileByBlock(src,dst);
    }
    /**
     * 下载文件
     * @param remote 远程文件
     * @param local 本地文件
     */
    public static void downloadFile(String remote,String local){
        ftpManager.downloadFile(remote,local);
    }
    /**
     * 下载文件
     * @param remote 远程文件
     * @param local 本地文件流
     */
    public static void downloadFile(String remote,OutputStream local){
        ftpManager.downloadFile(remote,local);
    }
    /**
     * 以块的方式下载文件
     * @param remote 远程文件
     * @param local 本地文件流
     */
    public static void downloadFileByBlock(String remote,OutputStream local){
        ftpManager.downloadFileByBlock(remote,local);
    }
    /**
     * 以块的方式下载文件
     * @param remote 远程文件
     * @param local 本地文件
     */
    public static void downloadFileByBlock(String remote,String local){
        ftpManager.downloadFileByBlock(remote,local);
    }
    /**
     * 删除服务器上文件
     * @param remote 远程文件
     */
    public static void removeFile(String remote){
        ftpManager.removeFile(remote);
    }
    /**
     * 删除服务器目录(删除无效)
     * @param dir 目录
     */
    public static void removeDir(String dir){
        ftpManager.removeDir(dir);
    }
    /**
     * 创建服务器目录
     * @param dir 目录
     */
    public static void mkdir(String dir){
        ftpManager.mkdir(dir);
    }

    /**
     * 注销、断开连接
     */
    public static void destory(){
        ftpManager.destory();
    }
    /**
     * 文件移动(重命名)
     * @param oldpath
     * @param newpath
     */
    public static void moveFile(String oldpath,String newpath){
        ftpManager.moveFile(oldpath,newpath);
    }

    /**
     * 目录下文件列表
     * @param path
     * @return
     */
    public static List<String> listFiles(String path){
        return ftpManager.listFile(path);
    }
}
