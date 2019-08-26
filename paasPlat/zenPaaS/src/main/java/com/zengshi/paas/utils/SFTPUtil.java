package com.zengshi.paas.utils;

import com.zengshi.paas.sftp.SFTPManagerSVC;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 */
public class SFTPUtil {
    private static SFTPManagerSVC sftpManager;
    static{
        sftpManager=PaasContextHolder.getContext().getBean("sftpManager",SFTPManagerSVC.class);
    }
    /**
     * 上传文件
     *
     * @param src  源文件流
     * @param dst  上传为目标文件
     * @param mode 上传模式  ChannelSftp.OVERWRITE 完全覆盖模式 ChannelSftp.RESUME 续传模式  ChannelSftp.APPEND 追加模式
     * @throws Exception
     */
    public static void uploadFile(InputStream src, String dst, int mode) throws Exception{
        sftpManager.uploadFile(src,dst,mode);
    }
    /**
     * 上传文件
     *
     * @param src  源文件
     * @param dst  上传为目标文件，若dst为目录，则目标文件名将与src文件名相同
     * @param mode 上传模式  ChannelSftp.OVERWRITE 完全覆盖模式 ChannelSftp.RESUME 续传模式  ChannelSftp.APPEND 追加模式
     * @throws Exception
     */
    public static void uploadFile(String src, String dst, int mode) throws Exception{
        sftpManager.uploadFile(src,dst,mode);
    }
    /**
     * 分块上传文件
     *
     * @param src  源文件
     * @param dst  上传为目标文件，若dst为目录，则目标文件名将与src文件名相同
     * @param mode 上传模式  ChannelSftp.OVERWRITE 完全覆盖模式 ChannelSftp.RESUME 续传模式  ChannelSftp.APPEND 追加模式
     * @throws Exception
     */
    public static void uploadFileByBlock(String src, String dst, int mode) throws Exception{
        sftpManager.uploadFileByBlock(src,dst,mode);
    }
    /**
     * 下载文件
     *
     * @param src 原文件
     * @return
     * @throws Exception
     */
    public static InputStream downloadFile(String src) throws Exception{
        return sftpManager.downloadFile(src);
    }
    /**
     * 下载文件
     *
     * @param src 原文件
     * @param dst 存储文件
     */
    public static void downloadFileByBlock(String src, String dst) throws Exception{
        sftpManager.downloadFileByBlock(src,dst);
    }
    /**
     * 下载文件
     *
     * @param src 原文件
     * @param dst 存储文件
     */
    public static void downloadFile(String src, String dst) throws Exception{
        sftpManager.downloadFile(src,dst);
    }
    /**
     * 下载文件
     *
     * @param src 原文件
     * @param dst 存储文件
     */
    public static void downloadFile(String src, OutputStream dst) throws Exception{
        sftpManager.downloadFile(src,dst);
    }
    /**
     * 删除文件
     * @param file
     * @throws Exception
     */
    public static void removeFile(String file) throws Exception{
        sftpManager.removeFile(file);
    }
    /**
     * 删除目录
     * @param dir
     * @throws Exception
     */
    public static void removeDir(String dir) throws Exception{
        sftpManager.removeDir(dir);
    }
    /**
     * 创建目录
     * @param dir
     * @throws Exception
     */
    public static void mkdir(String dir) throws Exception{
        sftpManager.mkdir(dir);
    }
    /**
     * 切换目录
     * @param dir
     * @throws Exception
     */
    public static void cdDir(String dir) throws Exception{
        sftpManager.cdDir(dir);
    }

    /**
     * 关闭通道
     * @throws Exception
     */
    public static void closeChannel() throws Exception{
        sftpManager.closeChannel();
    }
    /**
     * 文件移动(重命名)
     * @param oldpath 原文件
     * @param newpath 新文件
     * @throws Exception
     */
    public static void moveFile(String oldpath,String newpath) throws Exception{
        sftpManager.moveFile(oldpath,newpath);
    }
    /**
     *列出目录下的文件
     * @param path
     * @throws Exception
     */
    public static List<String> listFiles(String path) throws Exception{
        return sftpManager.listFiles(path);
    }
}
