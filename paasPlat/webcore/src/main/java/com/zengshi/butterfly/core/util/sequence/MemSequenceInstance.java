package com.zengshi.butterfly.core.util.sequence;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.zengshi.butterfly.core.util.ConfigUtil;

public class MemSequenceInstance implements ISequenceInstance {

	private static Map<String,Long> sequences=new ConcurrentHashMap<String, Long>();
	
	private static final String DIR_SEQ_TMP="./TEMP_SEQ";
	private static final String KEY_SEQ_AVLIABLE="SEQ_AVALIABLE";
	static {
		init();
	}
	
	private static void init() {
		 String keys=ConfigUtil.getValue(KEY_SEQ_AVLIABLE);
		 String[] seqs=keys==null?null:keys.split(";");
		 if(seqs != null) {
			 for(String seq:seqs) {
				 sequences.put(seq, -1l);
			 }
		 }
	}
	@Override
	public Long getNext(String name) {
		return this.getNext(name, 1);
	}

	@Override
	public Long getNext(String name, int pertimes) {
		Long currentId=sequences.get(name);
		sequences.put(name, currentId+pertimes);
		return currentId+pertimes;
	}

	@Override
	public void setValue(String name, Long value) {
		sequences.put(name, value);
		
	}

}
