package com.distribute.tx.common;

public class TransactionStatus {
	private boolean rollbackOnly = false;
	
	public boolean isRollbackOnly() {
		return rollbackOnly;
	}
	
	public void setRollbackOnly() {
		rollbackOnly = true;
	}
}
