package com.zengshi.paas.ftp;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 */
public interface FTPManagerSVC {

    void disconnect();
    void logout();
    void uploadFile(String src,String dst);
    void uploadFile(InputStream src, String dst);
    void uploadFileByBlock(String src,String dst);
    void uploadFileByBlock(InputStream src,String dst);
    void downloadFile(String remote,String local);
    void downloadFile(String remote,OutputStream local);
    void downloadFileByBlock(String remote,OutputStream local);
    void downloadFileByBlock(String remote,String local);
    void removeFile(String remote);
    void removeDir(String dir);
    void mkdir(String dir);
    void destory();
    void moveFile(String oldpath,String newpath);
    List<String> listFile(String path);
}
