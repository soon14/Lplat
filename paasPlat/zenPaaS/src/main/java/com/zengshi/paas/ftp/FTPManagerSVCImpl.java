package com.zengshi.paas.ftp;

import com.zengshi.paas.ConfigurationCenter;
import com.zengshi.paas.ConfigurationWatcher;
import com.zengshi.paas.PaasException;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**  {host:'127.0.0.1',port:21,username:'admin',password:'d6061d343369479f',workDir:'',encoding:'utf-8',timeout:60000}
 */
public class FTPManagerSVCImpl implements ConfigurationWatcher,FTPManagerSVC {
    private final static Logger logger=Logger.getLogger(FTPManagerSVCImpl.class);
    private final static String FTP_HOST="host";
    private final static String FTP_PORT="port";
    private final static String FTP_USERNAME="username";
    private final static String FTP_PASSWORD="password";
    private final static String FTP_WORKDIR="workDir";
    private final static String FTP_ENCODING="encoding";
    private final static String FTP_TIMEOUT="timeout";

    private String host;
    private int port=21;
    private String username;
    private String password;
    private String workDir;
    private String encoding="utf-8";
    private int timeout=60000;

    private FTPClientManager ftpClient;
    private ConfigurationCenter cc = null;
    private String confPath = "/com/zengshi/paas/ftp/conf";

    public void init() {
        try {
            process(cc.getConfAndWatch(confPath, this));

        } catch (PaasException e) {
            logger.error("sftp 初始化失败。",e);
        }
    }
    @Override
    public void process(String conf) {
        if(logger.isInfoEnabled()) {
            logger.info("new ftp configuration is received: " + conf);
        }

        JSONObject json = JSONObject.fromObject(conf);
        boolean changed = false;
        if(json.getString(FTP_HOST) != null && !json.getString(FTP_HOST).equals(host)) {
            changed = true;
            host = json.getString(FTP_HOST);
        }
        if(json.getString(FTP_PORT)!=null && json.getInt(FTP_PORT)!=port){
            changed=true;
            port=json.getInt(FTP_PORT);
        }
        if(json.getString(FTP_USERNAME)!=null && !json.getString(FTP_USERNAME).equals(username)){
            changed=true;
            username=json.getString(FTP_USERNAME);
        }
        if(json.getString(FTP_PASSWORD)!=null && !json.getString(FTP_PASSWORD).equals(password)){
            changed=true;
            password=json.getString(FTP_PASSWORD);
        }
        if(json.getString(FTP_WORKDIR)!=null && !json.getString(FTP_WORKDIR).equals(workDir)){
            changed=true;
            workDir=json.getString(FTP_WORKDIR);
        }
        if(json.getString(FTP_ENCODING)!=null && !json.getString(FTP_ENCODING).equals(encoding)){
            changed=true;
            encoding=json.getString(FTP_ENCODING);
        }
        if(json.getString(FTP_TIMEOUT)!=null && json.getInt(FTP_TIMEOUT)>0){
            changed=true;
            timeout=json.getInt(FTP_TIMEOUT);
        }
        if(changed){
            if(host != null) {
                if(null!=ftpClient){
                    destory();
                }
                ftpClient=new FTPClientManager(host,port,username,password,workDir);
                if(StringUtils.hasText(encoding)){
                    ftpClient.setEncoding(encoding);
                }
                if(timeout>0){
                    ftpClient.setTimeout(timeout);
                }
            }
        }
    }

    public void setCc(ConfigurationCenter cc) {
        this.cc = cc;
    }

    public void setConfPath(String confPath) {
        this.confPath = confPath;
    }

    @Override
    public void destory() {
        logout();
        disconnect();
    }

    @Override
    public void disconnect() {
        ftpClient.disconnect();
    }

    @Override
    public void logout() {
        ftpClient.logout();
    }

    @Override
    public void uploadFile(String src, String dst) {
        ftpClient.uploadFile(src,dst);
    }

    @Override
    public void uploadFile(InputStream src, String dst) {
        ftpClient.uploadFile(src,dst);
    }

    @Override
    public void uploadFileByBlock(String src, String dst) {
        ftpClient.uploadFileByBlock(src,dst);
    }

    @Override
    public void uploadFileByBlock(InputStream src, String dst) {
        ftpClient.uploadFileByBlock(src,dst);
    }

    @Override
    public void downloadFile(String remote, String local) {
        ftpClient.downloadFile(remote,local);
    }

    @Override
    public void downloadFile(String remote, OutputStream local) {
        ftpClient.downloadFile(remote,local);
    }

    @Override
    public void downloadFileByBlock(String remote, OutputStream local) {
        ftpClient.downloadFileByBlock(remote,local);
    }

    @Override
    public void downloadFileByBlock(String remote, String local) {
        ftpClient.downloadFileByBlock(remote,local);
    }

    @Override
    public void removeFile(String remote) {
        ftpClient.removeFile(remote);
    }

    @Override
    public void removeDir(String dir) {
        ftpClient.removeDir(dir);
    }

    @Override
    public void mkdir(String dir) {
        ftpClient.mkdir(dir);
    }

    @Override
    public void moveFile(String oldpath, String newpath) {
        ftpClient.moveFile(oldpath,newpath);
    }

    @Override
    public List<String> listFile(String path) {
        return ftpClient.listFiles(path);
    }
}
