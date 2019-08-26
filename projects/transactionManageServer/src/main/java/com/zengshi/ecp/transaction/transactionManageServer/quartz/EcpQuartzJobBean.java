package com.zengshi.ecp.transaction.transactionManageServer.quartz;

import com.zengshi.ecp.transaction.transactionManageServer.util.EcpFrameContextHolder;
import com.distribute.tx.server.AbnormalTransactionProcessor;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 */
public class EcpQuartzJobBean extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap=context.getJobDetail().getJobDataMap();
        AbnormalTransactionProcessor abnormalTransactionProcessor= (AbnormalTransactionProcessor) EcpFrameContextHolder.getBean((String)jobDataMap.get("targetObject"));
        abnormalTransactionProcessor.processAbnormalTransaction();
    }
}
