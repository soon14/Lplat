package com.zengshi.paas.ftp;

import com.zengshi.paas.utils.CipherUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.ftp.FtpClientFactory;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class FTPClientManager {
    private static final Logger logger=Logger.getLogger(FTPClientManager.class);
    private String host;
    private int port;
    private String username;
    private String password;
    private String workDir;
    private FTPClient ftpClient;

    public FTPClientManager(String host, int port, String username, String password, String workDir){
        this.host=host;
        this.port=port;
        this.username=username;
        this.password=password;
        this.workDir=workDir;
    }

    public FTPClient getClient() {
        if(null!=ftpClient){
            return ftpClient;
        }
        try {
            ftpClient = FtpClientFactory.createConnection(host, port, username.toCharArray(),
                        CipherUtil.decrypt(password).toCharArray(), workDir, new FileSystemOptions());
            ftpClient.enterLocalPassiveMode();//被动上传模式
        } catch (FileSystemException e) {
            logger.error("ftp服务 "+host+":"+port+"连接失败！",e);
        }
        return ftpClient;
    }

    /**
     * 断开连接
     */
    public void disconnect(){
        FTPClient client=getClient();
        try {
            client.disconnect();
        } catch (IOException e) {
            logger.error("ftpclient disconnect error!",e);
        }
    }

    /**
     * 注销
     */
    public void logout(){
        FTPClient client=getClient();
        try {
            client.logout();
        } catch (IOException e) {
            logger.error("ftpclient logout error!",e);
        }
    }

    /**
     * 上传文件
     * @param src 源文件
     * @param dst 目标文件
     */
    public void uploadFile(String src,String dst){
        InputStream ips=null;
        try {
            ips=new FileInputStream(src);
            uploadFile(ips,dst);
        } catch (IOException e) {
            logger.error("文件上传失败。"+src,e);
        }finally {
            if(null!=ips){
                try {
                    ips.close();
                } catch (IOException e) {
                    logger.error("文件close失败。");
                }
            }
        }
    }

    /**
     * 上传文件
     * @param src 源文件流
     * @param dst 目标文件
     */
    public void uploadFile(InputStream src,String dst){
        FTPClient client=getClient();
        try {
            client.storeFile(dst,src);
//            client.storeUniqueFile(dst,src);
        } catch (IOException e) {
            logger.error("文件上传失败。"+src,e);
        }
    }

    /**
     * 以文件块的方式上传文件
     * @param src 源文件
     * @param dst 目标文件
     */
    public void uploadFileByBlock(String src,String dst){
        InputStream ips=null;
        try {
            ips=new FileInputStream(src);
            uploadFileByBlock(ips,dst);
        } catch (FileNotFoundException e) {
            logger.error("文件上传失败.",e);
        }finally {
            if(null!=ips){
                try {
                    ips.close();
                } catch (IOException e) {
                    logger.error("文件流关闭失败.",e);
                }
            }
        }
    }

    /**
     * 以块的方式上传文件
     * @param src 源文件流
     * @param dst 目标文件
     */
    public void uploadFileByBlock(InputStream src,String dst){
        FTPClient client=getClient();
        OutputStream ops=null;
        try {
            ops=client.storeFileStream(dst);
            byte[] buff = new byte[1024 * 2];
            int read=0;
            if (src != null) {
                do{
                    read=src.read(buff, 0, buff.length);
                    if(read>0){
                        ops.write(buff,0,read);
                    }
                    ops.flush();
                }while(read>=0);
            }
        }catch (IOException e){
            logger.error("文件上传失败.",e);
        }finally {
            if(null!=ops){
                try {
                    ops.close();
                } catch (IOException e) {
                    logger.error("文件流关闭失败。",e);
                }
            }
        }
    }

    /**
     * 下载文件
     * @param remote 远程文件
     * @param local 本地文件
     */
    public void downloadFile(String remote,String local){
        OutputStream ops=null;
        try {
            ops=new FileOutputStream(local);
            downloadFile(remote,ops);
        } catch (FileNotFoundException e) {
            logger.error("文件上传失败。",e);
        }finally {
            if(ops!=null){
                try {
                    ops.close();
                } catch (IOException e) {
                    logger.error("文件流close失败。",e);
                }
            }
        }
    }

    /**
     * 下载文件
     * @param remote 远程文件
     * @param local 本地文件流
     */
    public void downloadFile(String remote,OutputStream local){
        FTPClient client=getClient();
        try {
            client.retrieveFile(remote,local);
        } catch (IOException e) {
            logger.error("文件上传失败。",e);
        }
    }

    /**
     * 以块的方式下载文件
     * @param remote 远程文件
     * @param local 本地文件流
     */
    public void downloadFileByBlock(String remote,OutputStream local){
        FTPClient client=getClient();
        InputStream ips=null;
        try {
            ips=client.retrieveFileStream(remote);
            byte[] buffer=new byte[1024*2];
            int read=0;
            do{
                read=ips.read(buffer,0,buffer.length);
                if(read>0){
                    local.write(buffer,0,read);
                }
                local.flush();
            }while(read>=0);
        } catch (IOException e) {
            logger.error("文件上传失败。",e);
        }finally {
            if(null!=ips){
                try {
                    ips.close();
                } catch (IOException e) {
                    logger.error("文件流close失败。",e);
                }
            }
        }
    }

    /**
     * 以块的方式下载文件
     * @param remote 远程文件
     * @param local 本地文件
     */
    public void downloadFileByBlock(String remote,String local){
        OutputStream ops=null;
        try {
            ops=new FileOutputStream(local);
            downloadFileByBlock(remote,ops);
        } catch (FileNotFoundException e) {
            logger.error("文件上传失败。",e);
        }finally {
            if(ops!=null){
                try {
                    ops.close();
                } catch (IOException e) {
                    logger.error("文件流close失败。",e);
                }
            }
        }
    }

    /**
     * 删除服务器上文件
     * @param remote 远程文件
     */
    public void removeFile(String remote){
        FTPClient client=getClient();
        try {
            client.deleteFile(remote);
        } catch (IOException e) {
            logger.error("删除文件失败.",e);
        }
    }

    /**
     * 删除服务器目录
     * @param dir 目录
     */
    public void removeDir(String dir){
        FTPClient client=getClient();
        try {
            client.dele(dir);
        } catch (IOException e) {
            logger.error("删除目录失败.",e);
        }
    }

    /**
     * 创建服务器目录
     * @param dir 目录
     */
    public void mkdir(String dir){
        FTPClient client=getClient();
        try {
            client.makeDirectory(dir);
        } catch (IOException e) {
            logger.error("创建目录失败.",e);
        }
    }

    /**
     * 设置编码
     * @param encoding
     */
    public void setEncoding(String encoding){
        if(!StringUtils.hasText(encoding)){
            return;
        }
        FTPClient client=getClient();
        client.setControlEncoding(encoding);
    }

    /**
     * 设置超时时间
     * @param timeout
     */
    public void setTimeout(int timeout){
        FTPClient client=getClient();
        client.setDataTimeout(timeout);
    }

    /**
     * 文件移动(重命名)
     * @param oldpath
     * @param newpath
     */
    public void moveFile(String oldpath,String newpath){
        FTPClient client=getClient();
        try {
            client.rename(oldpath,newpath);
        } catch (IOException e) {
            logger.error("文件移动失败.",e);
        }
    }
    /**
     *列出目录下的文件
     * @param path
     * @throws Exception
     */
    public List<String> listFiles(String path){
        FTPClient client=getClient();
        List<String> list=new ArrayList<String>();
        try {
            FTPFile[] files=client.listFiles(path);
            for(FTPFile file : files){
                if(!file.isDirectory()){
                    list.add(file.getName());
                }
            }
        } catch (IOException e) {
            logger.error("文件列表失败.",e);
        }
        return list;
    }

    public static void main(String[] args){
        FTPClientManager client=new FTPClientManager("192.168.111.218",2121,"admin","d6061d343369479f","");
//        client.uploadFileByBlock("E:\\a.xlsx","c.xlsx");
//        client.downloadFile("c.xlsx","e:\\1.xlsx");
//        client.downloadFileByBlock("b.xlsx","e:\\2.xlsx");
//        client.uploadFile("E:\\a.xlsx","b.xlsx");
//        client.mkdir("bb");
//        client.removeDir("bb");
//        client.removeFile("b.xlsx");
//        client.logout();
//        client.disconnect();
//        client.uploadFile("E:\\a.xlsx","b.xlsx");
        List<String> files=client.listFiles("aa");
        for (String file : files){
            System.out.println("=====================  "  + file);
        }
    }
}
