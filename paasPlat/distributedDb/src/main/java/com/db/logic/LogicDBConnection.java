package com.db.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.db.base.ConnectionBase;
import com.db.common.ConnectionManager;
import com.db.common.DistributedTransactionManager;

import net.sf.json.JSONObject;

/**
 * 逻辑库的Connection类
 * @date 2014年6月23日 上午9:57:47 
 * @version V1.0
 */
public class LogicDBConnection extends ConnectionBase {
	public static final Logger log = Logger.getLogger(LogicDBConnection.class);
	private JSONObject dbRule;
	private ConnectionManager manager = null;
	
	private LogicDBDataSource ds;
	
	public LogicDBConnection() {
		manager = new ConnectionManager();
	}
	
	@Override
	public Statement createStatement() throws SQLException {
		if(this.isClosed()) {
			return null;
		}
		LogicDBStatement e = new LogicDBStatement();
		e.setDbRule(dbRule);
		e.setManager(manager);
		e.setDs(this.getDs());
		e.setConnection(this);
		return e;
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		if(this.isClosed()) {
			return null;
		}
		LogicDBPreparedStatement ps = new LogicDBPreparedStatement(sql);
		ps.setDbRule(dbRule);
		ps.setManager(manager);
		ps.setDs(this.getDs());
		ps.setConnection(this);
		return ps;
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException {
		if(this.isClosed()) {
			return null;
		}
		LogicDBStatement e = (LogicDBStatement)createStatement();
		e.setResultSetType(resultSetType);
		e.setResultSetConcurrency(resultSetConcurrency);
		return e;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		if(this.isClosed()) {
			return null;
		}
		LogicDBPreparedStatement ps = (LogicDBPreparedStatement)prepareStatement(sql);
		ps.setResultSetType(resultSetType);
		ps.setResultSetConcurrency(resultSetConcurrency);
		ps.setDs(this.getDs());
		return ps;
	}

	@Override
	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		if(this.isClosed()) {
			return null;
		}
		LogicDBStatement e = (LogicDBStatement)createStatement();
		e.setResultSetType(resultSetType);
		e.setResultSetConcurrency(resultSetConcurrency);
		e.setResultSetHoldability(resultSetHoldability);
		return e;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		if(this.isClosed()) {
			return null;
		}
		LogicDBPreparedStatement ps = (LogicDBPreparedStatement)prepareStatement(sql);
		ps.setResultSetType(resultSetType);
		ps.setResultSetConcurrency(resultSetConcurrency);
		ps.setResultSetHoldability(resultSetHoldability);
		ps.setDs(this.getDs());
		return ps;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {
		if(this.isClosed()) {
			return null;
		}
		LogicDBPreparedStatement ps = (LogicDBPreparedStatement)prepareStatement(sql);
		ps.setAutoGeneratedKeys(autoGeneratedKeys);
		ps.setDs(this.getDs());
		return ps;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			throws SQLException {
		if(this.isClosed()) {
			return null;
		}
		LogicDBPreparedStatement ps = (LogicDBPreparedStatement)prepareStatement(sql);
		ps.setColumnIndexes(columnIndexes);
		ps.setDs(this.getDs());
		return ps;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames)
			throws SQLException {
		if(this.isClosed()) {
			return null;
		}
		LogicDBPreparedStatement ps = (LogicDBPreparedStatement)prepareStatement(sql);
		ps.setColumnNames(columnNames);
		ps.setDs(this.getDs());
		return ps;
	}

	@Override
	public void close() throws SQLException {
		if(DistributedTransactionManager.isInTransaction()) {
			if(log.isDebugEnabled()) {
				log.debug(this + " is in transaction, abandon closing.");
			}
			return;
		}
		if(log.isDebugEnabled()) {
			log.debug(this + " is closed.");
		}
		if(manager.getStatementList() != null && manager.getStatementList().size() > 0) {
			for(Statement e : manager.getStatementList()) {
				try {
					e.close();
					if(log.isDebugEnabled()){
						log.debug("==================公共库游标关闭=======================");
					}
				} catch (Exception e1) {
					log.error(e1.getMessage(),e1);
				}
			}
			manager.setStateList(new ArrayList<Statement>());
		}
		if(manager.getConnMap() != null && manager.getConnMap().size() > 0) {
			for(Connection conn : manager.getConnMap().values()) {
				try {
					if(log.isDebugEnabled()) {
						log.debug("close conn:" + conn);
					}
					conn.close();
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}
			}
		}
		closed = true;
	}

	@Override
	public void commit() throws SQLException {
		if(this.getAutoCommit()) {
			return;
		}
		if(log.isDebugEnabled()) {
			log.debug(this + " is commited.");
		}
		if(manager.getConnMap() != null && manager.getConnMap().size() > 0) {
			for(Connection conn : manager.getConnMap().values()) {
				try {
					conn.commit();
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}
			}
		}
	}

	@Override
	public void rollback() throws SQLException {
		if(this.getAutoCommit()) {
			return;
		}
		if(log.isDebugEnabled()) {
			log.debug(this + " is rollbacked.");
		}
		if(manager.getConnMap() != null && manager.getConnMap().size() > 0) {
			for(Connection conn : manager.getConnMap().values()) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}
			}
		}
	}

	public JSONObject getDbRule() {
		return dbRule;
	}

	public void setDbRule(JSONObject dbRule) {
		this.dbRule = dbRule;
	}

	public ConnectionManager getManager() {
		return manager;
	}

	public void setManager(ConnectionManager manager) {
		this.manager = manager;
	}

	public LogicDBDataSource getDs() {
		return ds;
	}

	public void setDs(LogicDBDataSource ds) {
		this.ds = ds;
	}

}
