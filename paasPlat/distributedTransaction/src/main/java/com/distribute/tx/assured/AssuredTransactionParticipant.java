package com.distribute.tx.assured;

import com.distribute.tx.common.TransactionStatus;

import net.sf.json.JSONObject;

public interface AssuredTransactionParticipant {
	
	public Object participantTransaction(JSONObject transactionContext, TransactionStatus status);
}
