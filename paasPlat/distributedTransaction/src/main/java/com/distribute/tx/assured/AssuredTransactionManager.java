package com.distribute.tx.assured;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.zengshi.paas.ConfigurationCenter;
import com.zengshi.paas.ConfigurationWatcher;
import com.zengshi.paas.PaasException;
import com.db.common.DistributedTransactionManager;
import com.db.sequence.Sequence;
import com.distribute.tx.common.ContextDecoder;
import com.distribute.tx.common.TransactionChecker;
import com.distribute.tx.common.TransactionContext;
import com.distribute.tx.common.TransactionPublisher;
import com.distribute.tx.common.TransactionStatus;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.producer.KeyedMessage;
import kafka.serializer.StringDecoder;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class AssuredTransactionManager  implements ConfigurationWatcher {
	
	public static final Logger log = Logger.getLogger(AssuredTransactionManager.class);
	
	private static final String ASSURED_TRANSACTION_MANAGER_TOPIC = "assured.transaction.manager.topic";
	private static final String ASSURED_TRANSACTION_CHECKER_TOPIC = "assured.transaction.checker.topic";
	private static final String ASSURED_TRANSACTION_CHECKER_NUM = "assured.transaction.checker.num";
	
	private String confPath = "/com/zpaas/tx/assuredTransactionManager";
	private ConfigurationCenter cc = null;
	private TransactionPublisher publisher = null;	
	private String transactionManagerTopic = "assured_transaction_manager_topic";	
	private String transactionCheckerTopic = "assured_transaction_checker_topic";
	private TransactionChecker transactionChecker = null;
	private int checkerThreadNum = 1;
	private ConsumerConnector consumer = null;
	private ExecutorService executor = null;
	private String participant = null;
	private Properties kafkaProps = null;
	private Sequence sequence = null;
	
	private HashMap<String, AssuredTransactionParticipant> subTransactions = null;
	private AssuredTransactionParticipant mainTransaction = null;
	private String transactionName = null;
	
	public void init() {
		if (log.isInfoEnabled()) {
			log.info("init AssuredTransactionManager...");
		}
		try {
			process(cc.getConfAndWatch(confPath, this));
		} catch (PaasException e) {
			e.printStackTrace();
		}
	}

	public void process(String conf) {
		if (log.isInfoEnabled()) {
			log.info("new AssuredTransactionManager configuration is received: " + conf);
		}
		JSONObject json = JSONObject.fromObject(conf);
		@SuppressWarnings("rawtypes")
		Iterator keys = json.keys();
		boolean changed = false;
		if (kafkaProps == null) {
			kafkaProps = new Properties();
			changed = true;
		}
		if (keys != null) {
			String key = null;
			while (keys.hasNext()) {
				key = (String) keys.next();
				if(ASSURED_TRANSACTION_MANAGER_TOPIC.equals(key)) {
					transactionManagerTopic = json.getString(key);
					continue;
				}else if(ASSURED_TRANSACTION_CHECKER_TOPIC.equals(key)) {
					String topic = json.getString(key);
					if(transactionCheckerTopic == null || !transactionCheckerTopic.equals(topic)) {
						transactionCheckerTopic = topic;
						changed = true;
					}
					continue;
				}else if(ASSURED_TRANSACTION_CHECKER_NUM.equals(key) ) {
					int n = json.getInt(key);
					if(n != this.checkerThreadNum) {
						this.checkerThreadNum = n;
						changed = true;
					}
					continue;
				}
				
				if (kafkaProps.containsKey(key)) {
					if (kafkaProps.get(key) == null
							|| !kafkaProps.get(key).equals(json.getString(key))) {
						kafkaProps.put(key, json.getString(key));
						changed = true;
					}
				} else {
					kafkaProps.put(key, json.getString(key));
					changed = true;
				}
			}
		}
		
		kafkaProps.put("group.id", participant);
		if(transactionChecker != null && (changed || consumer == null)) {
			stopOld(executor, consumer);
			ConsumerConfig cfg = new ConsumerConfig(kafkaProps);
			consumer = Consumer.createJavaConsumerConnector(cfg);
			startTransactionListener();
		}
	}
	
	public void startTransactionListener() {
		if(log.isInfoEnabled()) {
			log.info("start AssuredTransactionCheckListener...");
		}
		if(transactionCheckerTopic == null) {
			return;
		}
		Map<String, Integer> topicMap = new HashMap<String, Integer>();
		topicMap.put(transactionCheckerTopic, checkerThreadNum);
		
		List<String> subsList = new ArrayList<String>();
		for(String sub : this.subTransactions.keySet()) {
			subsList.add(sub);
			topicMap.put(this.transactionName + "_abnormal_" + sub, checkerThreadNum);
		}
		
		Map<String, List<KafkaStream<String,TransactionContext>>> topicMessageStreams = 
				consumer.createMessageStreams(topicMap, new StringDecoder(null), new ContextDecoder(null));
		executor = Executors.newFixedThreadPool(checkerThreadNum * topicMap.size());
		int i=0;
		for(String topic : topicMap.keySet()) {
			List<KafkaStream<String,TransactionContext>> streams = topicMessageStreams.get(topic);			
			for(final KafkaStream<String,TransactionContext> stream : streams) {
				if(i<checkerThreadNum) {
					executor.execute(new AssuredTransactionCheckProcessor(i, stream, publisher, 
						transactionManagerTopic, transactionChecker));
				}else {
					String sub = subsList.get((i-checkerThreadNum)/checkerThreadNum);
					executor.execute(new AssuredAbnormalTransactionProcessor(i, stream, publisher, 
							transactionManagerTopic, subTransactions.get(sub), sub));
				}
				i++;
			}
		}
	}
	
	
	public void stopOld(ExecutorService oldExecutor, ConsumerConnector oldConsumer) {
		if(log.isInfoEnabled()) {
			log.info("stop Old...");
		}
		if(oldConsumer != null) {
			if(log.isDebugEnabled()) {
				log.debug("old consumer is closed: " + oldConsumer);
			}
			oldConsumer.shutdown();
		}
		if(oldExecutor != null) {
			if(log.isDebugEnabled()) {
				log.debug("begin to close old executor: " + oldExecutor);
			}
			oldExecutor.shutdown();
			try {
				while(!oldExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
					if(log.isDebugEnabled()) {
						log.debug("old executor is not closed: " + oldExecutor);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(log.isDebugEnabled()) {
				log.debug("old executor is closed: " + oldExecutor);
			}
		}
	}
	
	
	
	public void execute(JSONObject transactionContext) {
		if(this.transactionName == null) {
			log.error("transaction name is null.");
			return ;
		}
		TransactionContext context = new TransactionContext();
		long id = sequence.nextValue();
		context.setTransactionId(id);
		context.setStartTime(System.currentTimeMillis());
		context.setFinishTime(0l);
		context.setStatus(TransactionContext.ASSURED_TRANSACTION_STATUS_NEW);
		context.setName(transactionName);
		context.setContent(transactionContext.toString());
		context.setTotalParticipants(JSONArray.fromObject(subTransactions.keySet()).toString());
		context.setParticipantAmount(subTransactions.size());
		KeyedMessage<String, TransactionContext> transactionMessage = 
				new KeyedMessage<String, TransactionContext>(transactionManagerTopic,String.valueOf(id), context);
		if(log.isInfoEnabled()) {
			log.info("initiate new transaction:" + context.getTransactionId());
		}
		if(!publisher.publish(transactionMessage)) {
			log.error("publish transaction failed: " + context.getTransactionId() + " new status:" + context.getStatus());
			return ;
		}
		AssuredTransactionExecutor executor = 
				new AssuredTransactionExecutor(transactionContext,mainTransaction,subTransactions);
		executor.setPublisher(publisher);
		context.setContent(null);
		executor.setContext(context);
		executor.setTransactionManagerTopic(transactionManagerTopic);
		executor.execute();
	}
	
	public HashMap<String, AssuredTransactionParticipant> getSubTransactions() {
		return subTransactions;
	}
	public void setSubTransactions(HashMap<String, AssuredTransactionParticipant> subTransactions) {
		this.subTransactions = subTransactions;
	}

	public String getConfPath() {
		return confPath;
	}

	public void setConfPath(String confPath) {
		this.confPath = confPath;
	}

	public ConfigurationCenter getCc() {
		return cc;
	}

	public void setCc(ConfigurationCenter cc) {
		this.cc = cc;
	}

	public Sequence getSequence() {
		return sequence;
	}

	public void setSequence(Sequence sequence) {
		this.sequence = sequence;
	}

	public TransactionPublisher getPublisher() {
		return publisher;
	}

	public void setPublisher(TransactionPublisher publisher) {
		this.publisher = publisher;
	}

	public TransactionChecker getTransactionChecker() {
		return transactionChecker;
	}

	public void setTransactionChecker(TransactionChecker transactionChecker) {
		this.transactionChecker = transactionChecker;
	}

	public String getParticipant() {
		return participant;
	}

	public void setParticipant(String participant) {
		this.participant = participant;
	}

	public AssuredTransactionParticipant getMainTransaction() {
		return mainTransaction;
	}

	public void setMainTransaction(AssuredTransactionParticipant mainTransaction) {
		this.mainTransaction = mainTransaction;
	}

	public String getTransactionName() {
		return transactionName;
	}

	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
	}
}

class AssuredTransactionCheckProcessor implements Runnable {
	public static final Logger log = Logger.getLogger(AssuredTransactionCheckProcessor.class);
	
	private String checkerName = null;
	
	private KafkaStream<String, TransactionContext> stream = null;	
	TransactionPublisher publisher = null;
	
	private String transactionManagerTopic = null;
	TransactionChecker transactionChecker;
	
	public AssuredTransactionCheckProcessor(int checkerId, KafkaStream<String, TransactionContext> stream, 
			TransactionPublisher publisher, String transactionManagerTopic, TransactionChecker transactionChecker) {
		this.checkerName = "TransactionCheckProcessor " + checkerId;
		this.stream = stream;
		this.publisher = publisher;
		this.transactionManagerTopic = transactionManagerTopic;
		this.transactionChecker = transactionChecker;
		if(log.isInfoEnabled()) {
			log.info(this.checkerName + " started");
		}
	}
	public void run() {
		ConsumerIterator<String, TransactionContext> it = stream.iterator();
		TransactionContext msg = null;
		while(it.hasNext() ) {
			try {
				msg = it.next().message();
				if(log.isDebugEnabled()) {
					log.debug(checkerName + " check transaction:" + msg.toString());
				}
				TransactionStatus status = new TransactionStatus();
				transactionChecker.checkTransaction(msg, status);
				if(status.isRollbackOnly()) {
					msg.setStatus(TransactionContext.TRANSACTION_STATUS_ROLLBACK);
					if(log.isDebugEnabled()) {
						log.debug("the transction:" + msg.getTransactionId() + " has bean rollbacked.");
					}
				}else {
					msg.setStatus(TransactionContext.TRANSACTION_STATUS_COMMIT);									
					if(log.isDebugEnabled()) {
						log.debug("the transction:" + msg.getTransactionId() + " has been commited.");
					}
				}
				KeyedMessage<String, TransactionContext> transactionMessage = 
						new KeyedMessage<String, TransactionContext>(transactionManagerTopic,
								String.valueOf(msg.getTransactionId()), msg);
				if(!publisher.publish(transactionMessage)) {
					log.error("publish transaction failed: " + msg.getTransactionId() + " new status:" + msg.getStatus());
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("exception:" + e);
			} catch (Error e) {
				e.printStackTrace();
				log.error("exception:" + e);
			}			
		}
		if(log.isInfoEnabled()) {
			log.info(checkerName + " is stopped");
		}
	}

}

class AssuredAbnormalTransactionProcessor implements Runnable {
	public static final Logger log = Logger.getLogger(AssuredAbnormalTransactionProcessor.class);
	
	private String processorName = null;
	private String subTransaction = null;
	
	private KafkaStream<String, TransactionContext> stream = null;	
	TransactionPublisher publisher = null;
	
	private String transactionManagerTopic = null;
	AssuredTransactionParticipant subs;
	
	public AssuredAbnormalTransactionProcessor(int processorId, KafkaStream<String, TransactionContext> stream, 
			TransactionPublisher publisher, String transactionManagerTopic, AssuredTransactionParticipant subs, String subTransaction) {
		this.processorName = "AssuredAbnormalTransactionProcessor " + processorId;
		this.stream = stream;
		this.publisher = publisher;
		this.transactionManagerTopic = transactionManagerTopic;
		this.subs = subs;
		this.subTransaction = subTransaction;
		if(log.isInfoEnabled()) {
			log.info(this.processorName + " started");
		}
	}
	public void run() {
		ConsumerIterator<String, TransactionContext> it = stream.iterator();
		TransactionContext msg = null;
		Connection subConn = null;
		while(it.hasNext() ) {
			try {
				msg = it.next().message();
				if(log.isDebugEnabled()) {
					log.debug(processorName + " process abnormal transaction:" + msg.toString());
				}
				AssuredTransactionStatus status = new AssuredTransactionStatus();
				JSONObject ctx = JSONObject.fromObject(msg.getContent());
				DistributedTransactionManager.beginTransaction();
				subs.participantTransaction(ctx, status);
				String subKey = (String)DistributedTransactionManager.getConnectionMap().keySet().iterator().next();
				subConn = DistributedTransactionManager.getConnectionMap().get(subKey);
				if(DistributedTransactionManager.getConnectionMap().size() != 1) {
					log.error("sub transaction only can be assigned with one connection:" + 
							DistributedTransactionManager.getConnectionMap());
					status.setRollbackOnly();
				}
				DistributedTransactionManager.unbindConnection(subKey);
				if(status.isRollbackOnly()) {
					try {
						subConn.rollback();
					} catch (Exception e) {
						e.printStackTrace();
						log.error("rollback transaction failed:" + subTransaction);
					}
					if(log.isDebugEnabled()) {
						log.debug("the sub transction:" + subTransaction + " of " + msg.getTransactionId() + " failed.");
					}
				}else {
					try {
						subConn.commit();
					} catch (Exception e) {
						e.printStackTrace();
						log.error("commit transaction failed.");
						return;
					}
					msg.setStatus(TransactionContext.ASSURED_TRANSACTION_STATUS_PART_FINISH);	
					msg.setParticipant(subTransaction);
					if(log.isDebugEnabled()) {
						log.debug("the sub transction:" + subTransaction + " of " + msg.getTransactionId() + " has been commited.");
					}
					KeyedMessage<String, TransactionContext> transactionMessage = 
							new KeyedMessage<String, TransactionContext>(transactionManagerTopic,
									String.valueOf(msg.getTransactionId()), msg);
					if(!publisher.publish(transactionMessage)) {
						log.error("publish transaction failed: " + msg.getTransactionId() + " new status:" + msg.getStatus());
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				log.error("exception:" + e);
			} catch (Error e) {
				e.printStackTrace();
				log.error("exception:" + e);
			} finally {
				if(subConn != null) {
					try {
						subConn.close();
					} catch (SQLException e) {
						e.printStackTrace();
						log.error("close connection failed.");
					}
				}
				DistributedTransactionManager.endTransaction();
			}
		}
		if(log.isInfoEnabled()) {
			log.info(processorName + " is stopped");
		}
	}

}
