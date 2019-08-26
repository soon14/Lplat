package com.distribute.tx.server;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.zengshi.paas.message.MessageStatus;
import com.distribute.tx.common.TransactionContext;
import com.distribute.tx.server.dao.TransactionContextDAO;



public class NewTransactionProcessor implements TransactionProcessor<TransactionContext>{
	public static Logger log = Logger.getLogger(NewTransactionProcessor.class);
	
	private TransactionContextDAO contextDAO = null;
	private TransactionTemplate tt = null;
	
	public void processTransaction(final TransactionContext context, final MessageStatus status) {
		
		tt.execute(new TransactionCallback() {
			
			@Override
			public Object doInTransaction(TransactionStatus paramTransactionStatus) {
				if(log.isInfoEnabled()) {
					log.info("save new transaction context to db:" + context.toString());
				}
				try {
					context.setStatusTime(new Date());
					context.setSendTimes(0);
					contextDAO.insert(context);
				} catch (Exception e) {
					e.printStackTrace();
					log.error("save new transaction context to db error: ",e);
				}
				return null;
			}
		});
		
	}

	public TransactionContextDAO getContextDAO() {
		return contextDAO;
	}

	public void setContextDAO(TransactionContextDAO contextDAO) {
		this.contextDAO = contextDAO;
	}

	public TransactionTemplate getTt() {
		return tt;
	}

	public void setTt(TransactionTemplate tt) {
		this.tt = tt;
	}
}
