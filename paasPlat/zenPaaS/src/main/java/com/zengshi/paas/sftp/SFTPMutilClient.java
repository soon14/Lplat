package com.zengshi.paas.sftp;

import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 */
public class SFTPMutilClient {

    private static Map<String,SFTPManagerSVC> sftpMap;

    public void setSftpMap(Map<String, SFTPManagerSVC> sftpMap) {
        SFTPMutilClient.sftpMap = sftpMap;
    }

    public static SFTPManagerSVC getSftpManager(String key){
        if(CollectionUtils.isEmpty(sftpMap)){
            throw new RuntimeException("请设置sftp连接对象。");
        }
        SFTPManagerSVC sftpManager=sftpMap.get(key);
        if(null==sftpManager){
            throw new RuntimeException("请设置sftp连接对象。");
        }

        return sftpManager;
    }

    /**
     * 上传文件
     *
     * @param src  源文件流
     * @param dst  上传为目标文件
     * @param mode 上传模式  ChannelSftp.OVERWRITE 完全覆盖模式 ChannelSftp.RESUME 续传模式  ChannelSftp.APPEND 追加模式
     * @throws Exception
     */
    public static void uploadFile(InputStream src, String dst, int mode,String key) throws Exception{
        getSftpManager(key).uploadFile(src,dst,mode);
    }
    /**
     * 上传文件
     *
     * @param src  源文件
     * @param dst  上传为目标文件，若dst为目录，则目标文件名将与src文件名相同
     * @param mode 上传模式  ChannelSftp.OVERWRITE 完全覆盖模式 ChannelSftp.RESUME 续传模式  ChannelSftp.APPEND 追加模式
     * @throws Exception
     */
    public static void uploadFile(String src, String dst, int mode,String key) throws Exception{
        getSftpManager(key).uploadFile(src,dst,mode);
    }
    /**
     * 分块上传文件
     *
     * @param src  源文件
     * @param dst  上传为目标文件，若dst为目录，则目标文件名将与src文件名相同
     * @param mode 上传模式  ChannelSftp.OVERWRITE 完全覆盖模式 ChannelSftp.RESUME 续传模式  ChannelSftp.APPEND 追加模式
     * @throws Exception
     */
    public static void uploadFileByBlock(String src, String dst, int mode,String key) throws Exception{
        getSftpManager(key).uploadFileByBlock(src,dst,mode);
    }
    /**
     * 下载文件
     *
     * @param src 原文件
     * @return
     * @throws Exception
     */
    public static InputStream downloadFile(String src,String key) throws Exception{
        return getSftpManager(key).downloadFile(src);
    }
    /**
     * 下载文件
     *
     * @param src 原文件
     * @param dst 存储文件
     */
    public static void downloadFileByBlock(String src, String dst,String key) throws Exception{
        getSftpManager(key).downloadFileByBlock(src,dst);
    }
    /**
     * 下载文件
     *
     * @param src 原文件
     * @param dst 存储文件
     */
    public static void downloadFile(String src, String dst,String key) throws Exception{
        getSftpManager(key).downloadFile(src,dst);
    }
    /**
     * 下载文件
     *
     * @param src 原文件
     * @param dst 存储文件
     */
    public static void downloadFile(String src, OutputStream dst,String key) throws Exception{
        getSftpManager(key).downloadFile(src,dst);
    }
    /**
     * 删除文件
     * @param file
     * @throws Exception
     */
    public static void removeFile(String file,String key) throws Exception{
        getSftpManager(key).removeFile(file);
    }
    /**
     * 删除目录
     * @param dir
     * @throws Exception
     */
    public static void removeDir(String dir,String key) throws Exception{
        getSftpManager(key).removeDir(dir);
    }
    /**
     * 创建目录
     * @param dir
     * @throws Exception
     */
    public static void mkdir(String dir,String key) throws Exception{
        getSftpManager(key).mkdir(dir);
    }
    /**
     * 切换目录
     * @param dir
     * @throws Exception
     */
    public static void cdDir(String dir,String key) throws Exception{
        getSftpManager(key).cdDir(dir);
    }

    /**
     * 关闭通道
     * @throws Exception
     */
    public static void closeChannel(String key) throws Exception{
        getSftpManager(key).closeChannel();
    }
    /**
     * 文件移动(重命名)
     * @param oldpath 原文件
     * @param newpath 新文件
     * @throws Exception
     */
    public static void moveFile(String oldpath,String newpath,String key) throws Exception{
        getSftpManager(key).moveFile(oldpath,newpath);
    }
    /**
     *列出目录下的文件
     * @param path
     * @throws Exception
     */
    public static List<String> listFiles(String path,String key) throws Exception{
        return getSftpManager(key).listFiles(path);
    }
}
