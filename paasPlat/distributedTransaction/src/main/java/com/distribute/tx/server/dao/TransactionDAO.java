package com.distribute.tx.server.dao;

import com.distribute.tx.common.Transaction;

public interface TransactionDAO {

	public Transaction getTransactionById(String name);
	public boolean insert(Transaction record);
	public int update(Transaction record);

}