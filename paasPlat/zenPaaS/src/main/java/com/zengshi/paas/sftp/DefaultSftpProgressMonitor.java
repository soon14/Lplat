package com.zengshi.paas.sftp;

import com.jcraft.jsch.SftpProgressMonitor;
import org.apache.log4j.Logger;

/**
 */
public class DefaultSftpProgressMonitor implements SftpProgressMonitor {
    private final static Logger logger=Logger.getLogger(DefaultSftpProgressMonitor.class);
    private long transfered=0;
    @Override
    public void init(int op, String src, String dst, long max) {
        if(logger.isDebugEnabled()){
            logger.debug("文件从"+src+"到"+dst+"开始上传或下载 。");
        }
    }

    @Override
    public boolean count(long count) {
        transfered+=count;
//        if(logger.isDebugEnabled()){
//            logger.debug("已上传或下载文件："+transfered/1024+" KB.");
//        }
        return true;
    }

    @Override
    public void end() {
        if(logger.isDebugEnabled()){
            logger.debug("文件上传或下载结束，大小："+transfered/1024+" KB.");
        }
    }
}
