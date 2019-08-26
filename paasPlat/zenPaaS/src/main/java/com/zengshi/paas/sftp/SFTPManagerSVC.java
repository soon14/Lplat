package com.zengshi.paas.sftp;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 */
public interface SFTPManagerSVC {

    void uploadFile(InputStream src, String dst, int mode) throws Exception;

    void uploadFile(String src, String dst, int mode) throws Exception;

    void uploadFileByBlock(String src, String dst, int mode) throws Exception;

    InputStream downloadFile(String src) throws Exception;

    void downloadFileByBlock(String src, String dst) throws Exception;

    void downloadFile(String src, String dst) throws Exception;

    void downloadFile(String src, OutputStream dst) throws Exception;

    void removeFile(String file) throws Exception;

    void removeDir(String dir) throws Exception;

    void mkdir(String dir) throws Exception;

    void cdDir(String dir) throws Exception;

    void closeChannel() throws Exception;

    void destory() throws Exception;

    void moveFile(String oldpath,String newpath) throws Exception;

    List<String> listFiles(String path) throws Exception;
}
