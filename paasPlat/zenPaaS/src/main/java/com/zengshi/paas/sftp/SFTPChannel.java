package com.zengshi.paas.sftp;

import com.zengshi.paas.utils.CipherUtil;
import com.jcraft.jsch.Channel;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 */
public class SFTPChannel {

    private final static Logger logger = Logger.getLogger(SFTPChannel.class);

    private Session session;
    private Channel channel;

    private String host = null;
    private String username = null;
    private String password = null;
    private int port = 22;
    private int timeout = 60000;

    public SFTPChannel(String host, String username, String password, int port, int timeout) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
        this.timeout = timeout;
    }

    public ChannelSftp getChannel() throws Exception {
        if (null != channel) {
            return (ChannelSftp) channel;
        }
        if (!(StringUtils.hasText(host) && StringUtils.hasText(username))) {
            throw new RuntimeException("host/username不能为null");
        }
        JSch jsch = new JSch(); // 创建JSch对象
        session = jsch.getSession(username, host, port); // 根据用户名，主机ip，端口获取一个Session对象
        if (logger.isDebugEnabled()) {
            logger.debug("Session created.");
        }
        if (StringUtils.hasText(password)) {
            String pwd = CipherUtil.decrypt(password);
            session.setPassword(pwd);
        }
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config); // 为Session对象设置properties
        session.setTimeout(timeout); // 设置timeout时间
        session.connect(); // 通过Session建立链接
        if (logger.isDebugEnabled()) {
            logger.debug("Session connected.");
            logger.debug("Opening Channel.");
        }

        channel = session.openChannel("sftp"); // 打开SFTP通道
        channel.connect(); // 建立SFTP通道的连接
        if (logger.isDebugEnabled()) {
            logger.debug("Connected successfully to host = " + host + ",as username = " + username + ", returning: " + channel);
        }
        return (ChannelSftp) channel;
    }

    /**
     * 上传文件
     *
     * @param src  源文件流
     * @param dst  上传为目标文件
     * @param mode 上传模式  ChannelSftp.OVERWRITE 完全覆盖模式 ChannelSftp.RESUME 续传模式  ChannelSftp.APPEND 追加模式
     * @throws Exception
     */
    public void uploadFile(InputStream src, String dst, int mode) throws Exception {
        ChannelSftp chSftp = getChannel();
        chSftp.put(src, dst, new DefaultSftpProgressMonitor(), mode);
    }

    /**
     * 上传文件
     *
     * @param src  源文件
     * @param dst  上传为目标文件，若dst为目录，则目标文件名将与src文件名相同
     * @param mode 上传模式  ChannelSftp.OVERWRITE 完全覆盖模式 ChannelSftp.RESUME 续传模式  ChannelSftp.APPEND 追加模式
     * @throws Exception
     */
    public void uploadFile(String src, String dst, int mode) throws Exception {
        ChannelSftp chSftp = getChannel();
        chSftp.put(src, dst, new DefaultSftpProgressMonitor(), mode);
    }

    /**
     * 分块上传文件
     *
     * @param src  源文件
     * @param dst  上传为目标文件，若dst为目录，则目标文件名将与src文件名相同
     * @param mode 上传模式  ChannelSftp.OVERWRITE 完全覆盖模式 ChannelSftp.RESUME 续传模式  ChannelSftp.APPEND 追加模式
     * @throws Exception
     */
    public void uploadFileByBlock(String src, String dst, int mode) throws Exception {
        ChannelSftp chSftp = getChannel();
        OutputStream out = chSftp.put(dst, new DefaultSftpProgressMonitor(), mode);
        byte[] buff = new byte[1024 * 8]; // 设定每次传输的数据块大小为256KB
        int read;
        if (out != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Start to read input stream");
            }
            InputStream is = new FileInputStream(src);
            do {
                read = is.read(buff, 0, buff.length);
                if (read > 0) {
                    out.write(buff, 0, read);
                }
                out.flush();
            } while (read >= 0);
            if (logger.isDebugEnabled()) {
                logger.debug("input stream read done.");
            }
        }


    }

    /**
     * 下载文件
     *
     * @param src 原文件
     * @return
     * @throws Exception
     */
    public InputStream downloadFile(String src) throws Exception {
        ChannelSftp chSftp = getChannel();
        return chSftp.get(src, new DefaultSftpProgressMonitor());

    }

    /**
     * 下载文件
     *
     * @param src 原文件
     * @param dst 存储文件
     */
    public void downloadFileByBlock(String src, String dst) throws Exception {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(dst);
            InputStream is = downloadFile(src);
            byte[] buff = new byte[1024 * 2];
            int read;
            if (is != null) {
                if(logger.isDebugEnabled()){
                    logger.debug("Start to read input stream");
                }
                do {
                    read = is.read(buff, 0, buff.length);
                    if (read > 0) {
                        out.write(buff, 0, read);
                    }
                    out.flush();
                } while (read >= 0);
                if(logger.isDebugEnabled()){
                    logger.debug("input stream read done.");
                }
            }
        } finally {
            if (null != out) {
                out.close();
            }
        }

    }

    /**
     * 下载文件
     *
     * @param src 原文件
     * @param dst 存储文件
     */
    public void downloadFile(String src, String dst) throws Exception {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(dst);
            downloadFile(src, out);
        } finally {
            if (null != out) {
                out.close();
            }
        }

    }

    /**
     * 下载文件
     *
     * @param src 原文件
     * @param dst 存储文件
     */
    public void downloadFile(String src, OutputStream dst) throws Exception {
        ChannelSftp chSftp = getChannel();
        chSftp.get(src, dst, new DefaultSftpProgressMonitor());
    }

    /**
     * 删除文件
     * @param file
     * @throws Exception
     */
    public void removeFile(String file) throws Exception{
        ChannelSftp chSftp = getChannel();
        chSftp.rm(file);
    }

    /**
     * 删除目录
     * @param dir
     * @throws Exception
     */
    public void removeDir(String dir) throws Exception{
        ChannelSftp chSftp = getChannel();
        chSftp.rmdir(dir);
    }

    /**
     * 创建目录
     * @param dir
     * @throws Exception
     */
    public void mkdir(String dir) throws Exception{
        ChannelSftp chSftp = getChannel();
        chSftp.mkdir(dir);
    }

    /**
     * 切换目录
     * @param dir
     * @throws Exception
     */
    public void cdDir(String dir) throws Exception{
        ChannelSftp chSftp = getChannel();
        chSftp.cd(dir);
    }
    /**
     * 关闭通道
     * @throws Exception
     */
    public void closeChannel() throws Exception {
        if (channel != null) {
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
    }

    /**
     * 文件移动(重命名)
     * @param oldpath 原文件
     * @param newpath 新文件
     * @throws Exception
     */
    public void moveFile(String oldpath,String newpath) throws Exception{
        ChannelSftp chSftp = getChannel();
        chSftp.rename(oldpath,newpath);
    }

    /**
     *列出目录下的文件
     * @param path
     * @throws Exception
     */
    public List<String> listFiles(String path) throws Exception{
        ChannelSftp chSftp = getChannel();
        List<ChannelSftp.LsEntry> entries=chSftp.ls(path);
        List<String> list=new ArrayList<String>();
        for(ChannelSftp.LsEntry entry : entries){
            if(!entry.getAttrs().isDir()){
                String filename=entry.getFilename();
                if(filename.startsWith(".")){
                    continue;
                }
                list.add(entry.getFilename());
            }
        }
        return list;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }


    public static void main(String[] args) {

        SFTPChannel sftpChannel = new SFTPChannel("192.168.111.217", "gzeport_dev01", "99303fe639a1c344e7a57518071e907cd51c8ddfa60ba0ca", 22, 60000);
        try {
            sftpChannel.uploadFile("e:\\a.xlsx","/home/gzeport_dev01",ChannelSftp.OVERWRITE);
//            sftpChannel.downloadFile("/home/gzeport_dev01/a.xlsx", "e:\\c.xlsx");
//            sftpChannel.downloadFileByBlock("/home/gzeport_dev01/a.xlsx", "e:\\d.xlsx");
//            sftpChannel.closeChannel();
//            sftpChannel.cdDir("/home/gzeport_dev01");
//            sftpChannel.downloadFile("a.xlsx", "e:\\e.xlsx");
//            sftpChannel.removeFile("a.xlsx");
//            sftpChannel.closeChannel();
            List<String> list=sftpChannel.listFiles("/home/gzeport_dev01");
            for(String file : list){
                System.out.println("======================================"+file);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
