package com.db.logic;

import com.db.base.ConnectionBase;
import com.db.base.StatementBase;
import com.db.distribute.DistributeRuleAssist;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * 逻辑库的Statement类
 * @date 2014年6月23日 上午9:58:40 
 * @version V1.0
 */
public class LogicDBStatement extends StatementBase {
	public static final Logger log = Logger.getLogger(LogicDBStatement.class);
	
	
	private JSONObject dbRule;	
	private LogicDBDataSource ds;

	public LogicDBStatement() {
		dbStmtList = new ArrayList<>();
	}

	public String selectDb() {
		int dbIndex = this.getDs().randomDbIndex();
		for(String dbName : this.getDs().getDbMap().keySet()) {
			if(dbIndex >= this.getDs().getWeightMap().get(dbName+"_min")  
					&& dbIndex < this.getDs().getWeightMap().get(dbName+"_max")) {
				return dbName;
			}
		}
		return null;
	}

	@Override
	public void close() throws SQLException {
		super.close();
		for (Statement stmt : dbStmtList) {
			stmt.close();
			if(null!=realSmt){
				realSmt.close();
				if(log.isDebugEnabled()){
					log.debug("===============LogicDBStatementBase close=================");
				}
			}
		}
//		dbStmtList.clear();
	}

	protected void processDbRule(String sql) {
		if(DistributeRuleAssist.isReadonly()) {
			setDbName(selectDb());
		}else {
			setDbName(getDbRule().getString("master"));
		}
		if(log.isTraceEnabled()) {
			log.trace("select db(" + this.getDbName() + ") to execute sql");
		}
	}
	
	public Connection getRealConnection(String dbName) throws SQLException {
		if(this.isClosed()) {
			return null;
		}
		Connection conn = manager.getConnection(dbName,this.getDs().getDbMap().get(dbName));
//		if(conn == null) {
//			BasicDataSource ds = (BasicDataSource)this.getDs().getDbMap().get(dbName);
//			conn = ds.getConnection();
//			manager.addConnection(dbName, conn);
//			if(log.isTraceEnabled()) {
//				log.trace("get new connection from  db: " + this.getDbName());
//			}
//		} else {
//			if(log.isTraceEnabled()) {
//				log.trace("get connection from connection manager, db: " + this.getDbName());
//			}
//		}
		if(conn == null) {
			throw new SQLException("failed to get connection from db: "+ this.getDbName());
		}
		((ConnectionBase)this.getConnection()).setWrappedConnection(conn);
		conn.setAutoCommit(((ConnectionBase)this.getConnection()).getAutoCommit());
		conn.setReadOnly(((ConnectionBase)this.getConnection()).isReadOnly());
		return conn;
	}
	
	@Override
	public Connection getRealConnection() throws SQLException {
		if(this.isClosed()) {
			return null;
		}
		Connection conn = manager.getConnection(dbName,this.getDs().getDbMap().get(dbName));
//		if(conn == null) {
//			DataSource ds = this.getDs().getDbMap().get(dbName);
//			conn = ds.getConnection();
//			manager.addConnection(dbName, conn);
//			if(log.isTraceEnabled()) {
//				log.trace("get new connection from  db: " + this.getDbName());
//			}
//		} else {
//			if(log.isTraceEnabled()) {
//				log.trace("get connection from connection manager, db: " + this.getDbName());
//			}
//		}
		if(conn == null) {
			throw new SQLException("failed to get connection from db: "+ this.getDbName());
		}
		((ConnectionBase)this.getConnection()).setWrappedConnection(conn);
		conn.setReadOnly(((ConnectionBase)this.getConnection()).isReadOnly());
		conn.setAutoCommit(((ConnectionBase)this.getConnection()).getAutoCommit());
		return conn;
	}

	protected JSONObject getDbRule() {
		return dbRule;
	}

	protected void setDbRule(JSONObject dbRule) {
		this.dbRule = dbRule;
	}

	public LogicDBDataSource getDs() {
		return ds;
	}

	public void setDs(LogicDBDataSource ds) {
		this.ds = ds;
	}

}
