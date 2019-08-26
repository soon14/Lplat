package com.zengshi.paas.ftp;

import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 */
public class FTPMutilClient {

    private static Map<String,FTPManagerSVC> ftpMap;

    public void setFtpMap(Map<String, FTPManagerSVC> ftpMap) {
        FTPMutilClient.ftpMap = ftpMap;
    }

    public static FTPManagerSVC getFtpManager(String key){
        if(CollectionUtils.isEmpty(ftpMap)){
            throw new RuntimeException("不存在ftp连接.");
        }
        FTPManagerSVC ftpManagerSVC=ftpMap.get(key);
        if(null==ftpManagerSVC){
            throw new RuntimeException("不存在ftp连接.");
        }

        return ftpManagerSVC;
    }

    /**
     * 断开连接
     */
    public  static void disconnect(String key){
        getFtpManager(key).disconnect();
    }
    /**
     * 注销
     */
    public static void logout(String key){
        getFtpManager(key).logout();
    }
    /**
     * 上传文件
     * @param src 源文件
     * @param dst 目标文件
     */
    public static void uploadFile(String src,String dst,String key){
        getFtpManager(key).uploadFile(src,dst);
    }
    /**
     * 上传文件
     * @param src 源文件流
     * @param dst 目标文件
     */
    public static void uploadFile(InputStream src, String dst,String key){
        getFtpManager(key).uploadFile(src,dst);
    }
    /**
     * 以文件块的方式上传文件
     * @param src 源文件
     * @param dst 目标文件
     */
    public static void uploadFileByBlock(String src,String dst,String key){
        getFtpManager(key).uploadFileByBlock(src,dst);
    }
    /**
     * 以块的方式上传文件
     * @param src 源文件流
     * @param dst 目标文件
     */
    public static void uploadFileByBlock(InputStream src,String dst,String key){
        getFtpManager(key).uploadFileByBlock(src,dst);
    }
    /**
     * 下载文件
     * @param remote 远程文件
     * @param local 本地文件
     */
    public static void downloadFile(String remote,String local,String key){
        getFtpManager(key).downloadFile(remote,local);
    }
    /**
     * 下载文件
     * @param remote 远程文件
     * @param local 本地文件流
     */
    public static void downloadFile(String remote,OutputStream local,String key){
        getFtpManager(key).downloadFile(remote,local);
    }
    /**
     * 以块的方式下载文件
     * @param remote 远程文件
     * @param local 本地文件流
     */
    public static void downloadFileByBlock(String remote,OutputStream local,String key){
        getFtpManager(key).downloadFileByBlock(remote,local);
    }
    /**
     * 以块的方式下载文件
     * @param remote 远程文件
     * @param local 本地文件
     */
    public static void downloadFileByBlock(String remote,String local,String key){
        getFtpManager(key).downloadFileByBlock(remote,local);
    }
    /**
     * 删除服务器上文件
     * @param remote 远程文件
     */
    public static void removeFile(String remote,String key){
        getFtpManager(key).removeFile(remote);
    }
    /**
     * 删除服务器目录(删除无效)
     * @param dir 目录
     */
    public static void removeDir(String dir,String key){
        getFtpManager(key).removeDir(dir);
    }
    /**
     * 创建服务器目录
     * @param dir 目录
     */
    public static void mkdir(String dir,String key){
        getFtpManager(key).mkdir(dir);
    }

    /**
     * 注销、断开连接
     */
    public static void destory(String key){
        getFtpManager(key).destory();
    }
    /**
     * 文件移动(重命名)
     * @param oldpath
     * @param newpath
     */
    public static void moveFile(String oldpath,String newpath,String key){
        getFtpManager(key).moveFile(oldpath,newpath);
    }

    /**
     * 目录下文件列表
     * @param path
     * @return
     */
    public static List<String> listFiles(String path,String key){
        return getFtpManager(key).listFile(path);
    }
}
