package com.distribute.tx.server;

import com.zengshi.paas.message.MessageStatus;


public interface  TransactionProcessor<T> {
	public void processTransaction(T context, MessageStatus status);
}
