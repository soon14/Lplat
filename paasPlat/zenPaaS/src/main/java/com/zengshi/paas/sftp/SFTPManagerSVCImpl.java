package com.zengshi.paas.sftp;

import com.zengshi.paas.ConfigurationCenter;
import com.zengshi.paas.ConfigurationWatcher;
import com.zengshi.paas.PaasException;
import com.google.common.util.concurrent.AtomicDouble;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**{host:'192.168.111.217',port:22,username:'gzeport_dev01',password:'99303fe639a1c344e7a57518071e907cd51c8ddfa60ba0ca',timeout:60000,rootPath:'/home/gzeport_dev01',threadNum:4}
 */
public class SFTPManagerSVCImpl implements ConfigurationWatcher,SFTPManagerSVC {

    private final static Logger logger=Logger.getLogger(SFTPManagerSVCImpl.class);

    private final static String SFTP_REQ_HOST = "host";
    private final static String SFTP_REQ_PORT = "port";
    private final static String SFTP_REQ_USERNAME = "username";
    private final static String SFTP_REQ_PASSWORD = "password";
    private final static String SFTP_SESSION_TIMEOUT="timeout";
    private final static String SFTP_ROOT_PATH="rootPath";
    private final static String SFTP_THREAD_NUM="threadNum";
    private static final AtomicDouble ad = new AtomicDouble(95);

    private String host=null;
    private String username=null;
    private String password=null;
    private int port=22;
    private int timeout=60000;
    private String rootPath=null;
    private int threadNum=4;
    private SFTPChannel sftpChannel=null;


    private ConfigurationCenter cc = null;
    private String confPath = "/com/zengshi/paas/sftp/conf";

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
            logger.info("new sftp configuration is received: " + conf);
        }
        JSONObject json = JSONObject.fromObject(conf);
        boolean changed = false;

        if(json.getString(SFTP_REQ_HOST) != null && !json.getString(SFTP_REQ_HOST).equals(host)) {
            changed = true;
            host = json.getString(SFTP_REQ_HOST);
        }
        if(json.getString(SFTP_REQ_PORT) != null && !json.getString(SFTP_REQ_PORT).equals(port)) {
            changed = true;
            port = json.getInt(SFTP_REQ_PORT);
        }
        if(json.getString(SFTP_REQ_USERNAME) != null && !json.getString(SFTP_REQ_USERNAME).equals(username)) {
            changed = true;
            username = json.getString(SFTP_REQ_USERNAME);
        }
        if(json.getString(SFTP_REQ_PASSWORD) != null && !json.getString(SFTP_REQ_PASSWORD).equals(password)) {
            changed = true;
            password = json.getString(SFTP_REQ_PASSWORD);
        }
        if(json.getString(SFTP_SESSION_TIMEOUT) != null && !json.getString(SFTP_SESSION_TIMEOUT).equals(timeout)) {
            changed = true;
            timeout = json.getInt(SFTP_SESSION_TIMEOUT);
        }
        if(json.getString(SFTP_ROOT_PATH) != null && !json.getString(SFTP_ROOT_PATH).equals(rootPath)) {
            changed = true;
            rootPath = json.getString(SFTP_ROOT_PATH);
        }
        if(json.getString(SFTP_THREAD_NUM) != null && !json.getString(SFTP_THREAD_NUM).equals(rootPath)) {
            changed = true;
            threadNum = json.getInt(SFTP_THREAD_NUM);
        }

        if(changed) {
            if(host != null) {
                if(null!=sftpChannel){
                    try {
                        sftpChannel.closeChannel();
                    } catch (Exception e) {
                        throw new RuntimeException("sftpChannel close error.",e);
                    }
                }
                sftpChannel = new SFTPChannel(host,username, password,port, timeout);
                if(StringUtils.hasText(rootPath)){
                    try {
                        sftpChannel.cdDir(rootPath);
                    } catch (Exception e) {
                        throw new RuntimeException("sftpChannel cd "+rootPath+" error.",e);
                    }
                }
                if(logger.isInfoEnabled()) {
                    logger.info("sftp connect " + host);
                }
            }
        }

    }

    /**
     * 生产FileId,长度为20； U/R+17位时间戳+2位序列
     *
     * @param needValidate
     *            是否需要权限验证
     * @return
     */
    private String genFileId(boolean needValidate) {
        StringBuffer fileid = new StringBuffer(20);

        if (needValidate) {
            fileid.append("R");
        } else {
            fileid.append("U");
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        fileid.append(formatter.format(new Date()));

		/*
		 * 为了防止在一毫秒内有并发，还需要带上两位序列号
		 */
        double idx = ad.getAndAdd(1);

        // SequenceUtil.getNextSequence("SEQ_FILEID");
        idx = idx % 99;
        DecimalFormat df = new DecimalFormat("00");
        fileid.append(df.format(idx));
        return fileid.toString();
    }

    @Override
    public void uploadFile(InputStream src, String dst, int mode) throws Exception {
        sftpChannel.uploadFile(src,dst,mode);
    }

    @Override
    public void uploadFile(String src, String dst, int mode) throws Exception {
        sftpChannel.uploadFile(src,dst,mode);
    }

    @Override
    public void uploadFileByBlock(String src, String dst, int mode) throws Exception {
        sftpChannel.uploadFileByBlock(src,dst,mode);
    }

    @Override
    public InputStream downloadFile(String src) throws Exception {
        return sftpChannel.downloadFile(src);
    }

    @Override
    public void downloadFileByBlock(String src, String dst) throws Exception {
        sftpChannel.downloadFileByBlock(src,dst);
    }

    @Override
    public void downloadFile(String src, String dst) throws Exception {
        sftpChannel.downloadFile(src,dst);
    }

    @Override
    public void downloadFile(String src, OutputStream dst) throws Exception {
        sftpChannel.downloadFile(src,dst);
    }

    @Override
    public void removeFile(String file) throws Exception {
        sftpChannel.removeFile(file);
    }

    @Override
    public void removeDir(String dir) throws Exception {
        sftpChannel.removeDir(dir);
    }

    @Override
    public void mkdir(String dir) throws Exception {
        sftpChannel.mkdir(dir);
    }

    @Override
    public void cdDir(String dir) throws Exception {
        sftpChannel.cdDir(dir);
    }


    @Override
    public void closeChannel() throws Exception {
        sftpChannel.closeChannel();
    }

    @Override
    public void destory() throws Exception {
        closeChannel();
    }

    @Override
    public void moveFile(String oldpath, String newpath) throws Exception {
        sftpChannel.moveFile(oldpath,newpath);
    }

    @Override
    public List<String> listFiles(String path) throws Exception {

        return sftpChannel.listFiles(path);
    }

    public void setCc(ConfigurationCenter cc) {
        this.cc = cc;
    }

    public void setConfPath(String confPath) {
        this.confPath = confPath;
    }
}
