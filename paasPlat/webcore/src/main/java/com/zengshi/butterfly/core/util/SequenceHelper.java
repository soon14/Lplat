/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zengshi.butterfly.core.util;

import com.zengshi.butterfly.core.config.ConfigManager;
import com.zengshi.butterfly.core.config.ConfigManagerDefault;
import com.zengshi.butterfly.core.config.center.CenterPriorityEnum;
import com.zengshi.butterfly.core.config.provider.PropertyConfigProvider;
import com.zengshi.butterfly.core.config.provider.XMLConfigProvider;
import com.zengshi.butterfly.core.util.sequence.ISequenceInstance;
import com.zengshi.butterfly.core.util.sequence.MemSequenceInstance;
import com.zengshi.butterfly.core.util.sequence.Sequence;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 */
public class SequenceHelper {

    private static ISequenceInstance sequenceInstance = null;
    private static Map<String,Sequence> seqConfig=new HashMap<String, Sequence>();
    public static void initInstance() {
        //TODO yangqx 加载sequence配置
    	sequenceInstance =new MemSequenceInstance();
        //TODO yangqx 加载实例
    	Sequence seq=new Sequence("myseq",10);
    	//Sequence seq2=new Sequence("myseq2",10);
    	seqConfig.put(seq.getName(), initSeq(seq));
    	//seqConfig.put(seq.getName(), initSeq(seq2));
    	
    	
    	//seqConfig.put(seq2.getName(), seq2);
    	
    }
    private static Sequence initSeq(Sequence seq) {
    	Long next=sequenceInstance.getNext(seq.getName());
    	seq.setCurrent(next);
    	return seq;
    }

    /**
     * FIXME luyidu该方法待删除
     * @return 
     */
    public static long getSequence_tempMethod(){
    	return System.currentTimeMillis()+ new Random().nextInt();
    }
    
    
    public static String getSequence(String prefix, String name) {
    	
        //sequence保存在文件或者数据库或者memcache中
    	Sequence seqConf=seqConfig.get(name);
    	
    	Long nextId=0l;
    	synchronized (seqConf) {
    		boolean loadnext =false;
    		if(seqConf.isMaxValue()) {
    			sequenceInstance.setValue(name, seqConf.getMin());
    			loadnext=true;
    		}
    		if(seqConf.isOvered()) {
    			loadnext=true;
    		}
    		if(loadnext) {
    			System.out.println(Thread.currentThread().getName()+"==load next cache...");
    			Long current=sequenceInstance.getNext(seqConf.getName(),seqConf.getCacheCount());
    			seqConf.setCurrent(current);
    			seqConf.setStart(current);
    			seqConf.setEnd(current+seqConf.getCacheCount());
    		}
    		nextId=seqConf.getNext();
    		
		}
    	
        return prefix+nextId;
    }

    public void switchInstance(String instanceName) {
    }
    
    public static void main(String[] arg) {
    		
		//默认提供的配置信息提供者
		try {
			ConfigManager configManager = ConfigManagerDefault.getInstance();
			configManager.addProvider(new PropertyConfigProvider(), CenterPriorityEnum.CUSTORMER);
			//configManager.addProvider(new XMLConfigProvider(), CenterPriorityEnum.CUSTORMER);
			configManager.build();
		} catch (Exception e) {
			
			e.printStackTrace();
		}

    	
    	SequenceHelper.initInstance();
    	/*for(int i=0;i<20;i++) {
    		String nextId=SequenceHelper.getSequence("pre_", "myseq");
        	System.out.println(nextId);
    	}*/
    	Thread t1=new Thread(new Runnable() {
			
			@Override
			public void run() {
				for(int i=0;i<2000;i++) {
		    		String nextId=SequenceHelper.getSequence("pre_", "myseq");
		    		System.out.println(Thread.currentThread().getName()+"=="+nextId);
		    	}
				
			}
		});
    	
    	Thread t2=new Thread(new Runnable() {
			
			@Override
			public void run() {
				for(int i=0;i<2000;i++) {
		    		String nextId=SequenceHelper.getSequence("pre_", "myseq");
		        	System.out.println(Thread.currentThread().getName()+"=="+nextId);
		    	}
				
			}
		});
    	Thread t3=new Thread(new Runnable() {
			
			@Override
			public void run() {
				for(int i=0;i<2000;i++) {
		    		String nextId=SequenceHelper.getSequence("pre_", "myseq");
		        	System.out.println(Thread.currentThread().getName()+"=="+nextId);
		    	}
				
			}
		});
    	t1.start();
    	t2.start();
    	t3.start();
    	
    	/*for(int i=0;i<30;i++) {
    		String nextId=SequenceHelper.getSequence("pre2_", "myseq2");
        	System.out.println(nextId);
    	}*/
    	
    }

}
