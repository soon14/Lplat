package com.distribute.tx.common;



public interface TransactionChecker {
	public void checkTransaction(TransactionContext message, TransactionStatus status);
}
