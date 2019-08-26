package com.distribute.tx.server.dao;

import java.util.List;

import com.distribute.tx.common.TransactionContext;

public interface TransactionContextDAO {

	public TransactionContext getTransactionContextById(Long transactionId);
	public boolean insert(TransactionContext record);
	public int update(TransactionContext record);
	
	public List<TransactionContext> queryAbnormalTransaction(long interval);
}