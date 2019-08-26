package com.distribute.tx.common;

import net.sf.json.JSONObject;


public interface TransactionListener {
	public void joinTransaction(JSONObject message, TransactionStatus status, String transctionName);
}
