package com.distribute.tx.common;



public interface TransactionCallback {
	
	public Object doInTransaction(TransactionStatus status);

}
