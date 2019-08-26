package com.zengshi.ecp.transaction.transactionManageServer;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.distribute.tx.server.TransactionManagerServer;

/**
 *事务管理器服务启动类
 *
 */
public class App 
{
    private final static Logger logger=Logger.getLogger(App.class);
    public static void main( String[] args )
    {
        Thread thread=new Thread(new Runnable() {
            
            @Override
            public void run() {
                ClassPathXmlApplicationContext ctx=new ClassPathXmlApplicationContext(new String[]{"classpath*:application-context.xml"});
                TransactionManagerServer server = (TransactionManagerServer)ctx.getBean("transactionManagerServer");
                ctx.start();
                logger.info("****************************事务管理器服务启动**********************************");
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(),e);
        }
    }
}
