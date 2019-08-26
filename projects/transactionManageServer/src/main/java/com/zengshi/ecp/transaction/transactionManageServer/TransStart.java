package com.zengshi.ecp.transaction.transactionManageServer;

import com.distribute.tx.server.TransactionManagerServer;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 */
public class TransStart {
    private final static Logger logger=Logger.getLogger(App.class);
    public static void main( String[] args )
    {
        ClassPathXmlApplicationContext ctx=new ClassPathXmlApplicationContext(new String[]{"classpath*:application-context.xml"});
        TransactionManagerServer server = (TransactionManagerServer)ctx.getBean("transactionManagerServer");
        ctx.start();
        logger.info("****************************事务管理器服务启动**********************************");
        while(true){
            try {
                Thread.currentThread().sleep(30L);
            } catch (InterruptedException e) {
                logger.error("事务服务器异常",e);
            }
        }
    }
}
