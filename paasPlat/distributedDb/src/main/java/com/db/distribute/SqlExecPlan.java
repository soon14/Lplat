package com.db.distribute;

import com.db.common.ConnectionManager;
import com.db.logic.LogicDBPreparedStatement;
import com.db.logic.LogicDBStatement;
import com.db.sql.ParsedSqlContext;
import com.db.sql.SQLType;
import com.db.sql.TableDistributeInfo;
import com.db.util.Constants;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.Reader;
import java.sql.*;
import java.util.*;

/**
 * 分布式数据库的SQL执行计划
 * @date 2014年6月23日 上午9:57:20 
 * @version V1.0
 */
public class SqlExecPlan {
	private static final Logger log = Logger.getLogger(SqlExecPlan.class);

	private List<TableDistributeInfo> distributeInfoList = null;
	private String sql = null;
	private Map<Integer,Object> valueMap = null;
	private Map<Integer, Integer> typeMap = null;
	private ConnectionManager manager = null;
	private DistributedStatement statement = null;
	private boolean prepared = false;
	private ParsedSqlContext ctx = null;
	private String dbType = null;

	public ResultSet executeQuery() throws SQLException{
		if(distributeInfoList == null || distributeInfoList.size() == 0) {
			throw new SQLException("can't get distribute info list");
		}
		if(sql == null || sql.length() == 0) {
			throw new SQLException("sql is null");
		}
		
		TableDistributeInfo keyTable = distributeInfoList.get(0);
		Integer tableIndex = DistributeRuleAssist.getTableIndex();
		if(tableIndex != null) {
			keyTable.setFullTableScan(1);			
		}
		DistributedResultSet drs = new DistributedResultSet();
		drs.setDbType(this.getDbType());
		drs.setCtx(ctx);
		drs.setStatement(statement);
		if(keyTable.getFullTableScan() == 1) {
			String sqlExec = sql;
			if(tableIndex != null) {
				if(SQLType.SHOW.equals(ctx.getSqlType())) {
					DistributedTableRule tableRule = statement.getDbRule().getTableRules().get(DistributedDBRule.DEFAULT_RULE);
					int dbId = DistributedDBRule.calculateDistributeId(
							JSONObject.fromObject(tableRule.getLogicDBRule()), tableIndex, statement.getDbRule().getMappingRules());
					statement.setDbName(DistributedDBRule.calculatePattern(dbId, tableRule.getLogicDBPattern()));
				}else {
					DistributedTableRule tableRule = statement.getDbRule().getTableRules().get(keyTable.getTableName());
					int tableId = DistributedDBRule.calculateDistributeId(
							JSONObject.fromObject(tableRule.getTableNameRule()), tableIndex, statement.getDbRule().getMappingRules());
					for(TableDistributeInfo info : distributeInfoList) {
						sqlExec = sqlExec.replaceAll("([\\s`])" + info.getTableName() + "([\\(\\s`])", 
								"$1" + DistributedDBRule.calculatePattern(tableId, keyTable.getTablePattern()) + "$2");
						sqlExec = sqlExec.replaceAll("(\\s)" + info.getTableName() + "$", 
								"$1" + DistributedDBRule.calculatePattern(tableId, keyTable.getTablePattern()));
					}
					int dbId = DistributedDBRule.calculateDistributeId(
							JSONObject.fromObject(tableRule.getLogicDBRule()), tableIndex, statement.getDbRule().getMappingRules());
					statement.setDbName(DistributedDBRule.calculatePattern(dbId, keyTable.getDbPattern()));
				}
			}else {
				for(TableDistributeInfo info : distributeInfoList) {
					sqlExec = sqlExec.replaceAll("([\\s`])" + info.getTableName() + "([\\(\\s`])", 
							"$1" + info.getPhysicalTableMap().values().iterator().next()+ "$2");
					sqlExec = sqlExec.replaceAll("(\\s)" + info.getTableName() + "$", 
							"$1" + info.getPhysicalTableMap().values().iterator().next());
				}
				statement.setDbName(keyTable.getLogicDbMap().values().iterator().next());
			}
			ResultSet rs = this.executeQueryInternal(statement.getDbName(), sqlExec);
			drs.mergeResultSet(rs);//TODO
			return drs;
		}else if(keyTable.getFullTableScan() > 1) {		
			HashSet<Object> tables = new HashSet<Object>();
			boolean flag = false;
			ArrayList<ExecQueryThread> tList = new ArrayList<ExecQueryThread>();
			Vector<ResultSet> v = new Vector<ResultSet>();
			for(String table : keyTable.getTableColValueMap().keySet()) {
				int i=0;
				flag = false;
				String sqlExec = sql;
				for(TableDistributeInfo info : distributeInfoList) {
					if(i==0) {
						if(tables.contains(info.getPhysicalTableMap().get(keyTable.getTableColValueMap().get(table)))) {
							flag = true;
							break;
						}else {
							tables.add(info.getPhysicalTableMap().get(keyTable.getTableColValueMap().get(table)));
						}
					}
					i++;
					sqlExec = sqlExec.replaceAll("([\\s`])" + info.getTableName() + "([\\(\\s`])", 
							"$1" + info.getPhysicalTableMap().get(keyTable.getTableColValueMap().get(table))+ "$2");
					sqlExec = sqlExec.replaceAll("(\\s)" + info.getTableName() + "$", 
							"$1" + info.getPhysicalTableMap().get(keyTable.getTableColValueMap().get(table)));
				}
				if(flag) {
					continue;
				}
				statement.setDbName(keyTable.getLogicDbMap().get(keyTable.getTableColValueMap().get(table)));
				ExecQueryThread t = new ExecQueryThread(this, sqlExec, statement.getDbName(), v);
				tList.add(t);
				t.start();
			}
			for(ExecQueryThread t : tList) {
				try {
					t.join();
				} catch (InterruptedException e) {
				}
			}
			for(ExecQueryThread t : tList) {
				if(!t.succeed) {
					throw new SQLException(t.getReason());
				}
			}
			for(ResultSet rs : v) {
				drs.mergeResultSet(rs);
			}
			return drs;
		}else if(keyTable.getFullTableScan() == -1) {			
			if(SQLType.SHOW.equals(ctx.getSqlType())) {
				throw new SQLException("sql can't run on more than one db: show talbes.");
			}else {
				DistributedTableRule tableRule = statement.getDbRule().getTableRules().get(keyTable.getTableName());
				JSONObject tableNameRule = JSONObject.fromObject(tableRule.getTableNameRule());
				ArrayList<ExecQueryThread> tList = new ArrayList<ExecQueryThread>();
				Vector<ResultSet> v = new Vector<ResultSet>();
				if("Mapping".equals(tableNameRule.getString("operator"))) {
					Map<Object, Integer> tbMap = statement.getDbRule().getMappingRules().get(tableNameRule.getString("operatorValue"));
					if(tbMap == null) {
						throw new SQLException("invalid distribute rule");
					}
					Iterator<Object> key = tbMap.keySet().iterator();
					
					while(key.hasNext()) {
						String sqlExec = sql;
						Object keyValue = key.next();
						int tableId = DistributedDBRule.calculateDistributeId(tableNameRule, keyValue, statement.getDbRule().getMappingRules());
						for(TableDistributeInfo info : distributeInfoList) {
							sqlExec = sqlExec.replaceAll("([\\s`])" + info.getTableName() + "([\\(\\s`])", 
									"$1" + DistributedDBRule.calculatePattern(tableId, keyTable.getTablePattern()) + "$2");
							sqlExec = sqlExec.replaceAll("(\\s)" + info.getTableName() + "$", 
									"$1" + DistributedDBRule.calculatePattern(tableId, keyTable.getTablePattern()));
						}
						int dbId = DistributedDBRule.calculateDistributeId(JSONObject.fromObject(tableRule.getLogicDBRule()), keyValue, statement.getDbRule().getMappingRules());
						statement.setDbName(DistributedDBRule.calculatePattern(dbId, keyTable.getDbPattern()));
						ExecQueryThread t = new ExecQueryThread(this, sqlExec, statement.getDbName(), v);
						tList.add(t);
						t.start();
					}
					
				}else {
					for(int i=0; i<tableRule.getSubTableCount(); i++) {
						String sqlExec = sql;
						int tableId = DistributedDBRule.calculateDistributeId(tableNameRule, i, statement.getDbRule().getMappingRules());
						for(TableDistributeInfo info : distributeInfoList) {
							sqlExec = sqlExec.replaceAll("([\\s`])" + info.getTableName() + "([\\(\\s`])", 
									"$1" + DistributedDBRule.calculatePattern(tableId, keyTable.getTablePattern()) + "$2");
							sqlExec = sqlExec.replaceAll("(\\s)" + info.getTableName() + "$", 
									"$1" + DistributedDBRule.calculatePattern(tableId, keyTable.getTablePattern()));
						}
						int dbId = DistributedDBRule.calculateDistributeId(JSONObject.fromObject(tableRule.getLogicDBRule()), i, statement.getDbRule().getMappingRules());
						statement.setDbName(DistributedDBRule.calculatePattern(dbId, keyTable.getDbPattern()));
						ExecQueryThread t = new ExecQueryThread(this, sqlExec, statement.getDbName(), v);
						tList.add(t);
						t.start();
					}
				}
				for(Thread t : tList) {
					try {
						t.join();
					} catch (InterruptedException e) {
					}
				}
				for(ExecQueryThread t : tList) {
					if(!t.succeed) {
						throw new SQLException(t.getReason());
					}
				}
				for(ResultSet rs : v) {
					drs.mergeResultSet(rs);
				}
			}			
			return drs;
		}else {
			throw new SQLException("invalid fullTableScan value:" + keyTable.getFullTableScan());
		}
	}
	
	public int executeUpdate(int autoGeneratedKeys, int[] columnIndexes, 
			String[] columnNames) throws SQLException{
		if(distributeInfoList == null || distributeInfoList.size() == 0) {
			throw new SQLException("can't get distribute info list");
		}
		if(sql == null || sql.length() == 0) {
			throw new SQLException("sql is null");
		}
		String sqlType = SQLType.getSqlType(sql);
		TableDistributeInfo keyTable = distributeInfoList.get(0);
		Integer tableIndex = DistributeRuleAssist.getTableIndex();
		if(tableIndex != null) {
			keyTable.setFullTableScan(1);			
		}
		if(keyTable.getFullTableScan() == 1) {
			String sqlExec = sql;
			if(tableIndex != null) {
				DistributedTableRule tableRule = statement.getDbRule().getTableRules().get(keyTable.getTableName());
				int tableId = DistributedDBRule.calculateDistributeId(JSONObject.fromObject(tableRule.getTableNameRule()), tableIndex, statement.getDbRule().getMappingRules());
				for(TableDistributeInfo info : distributeInfoList) {
					sqlExec = sqlExec.replaceAll("([\\s`])" + info.getTableName() + "([\\(\\s`])", 
							"$1" + DistributedDBRule.calculatePattern(tableId, keyTable.getTablePattern()) + "$2");
					sqlExec = sqlExec.replaceAll("(\\s)" + info.getTableName() + "$", 
							"$1" + DistributedDBRule.calculatePattern(tableId, keyTable.getTablePattern()));
				}
				int dbId = DistributedDBRule.calculateDistributeId(JSONObject.fromObject(tableRule.getLogicDBRule()), tableIndex, statement.getDbRule().getMappingRules());
				statement.setDbName(DistributedDBRule.calculatePattern(dbId, keyTable.getDbPattern()));
			}else {
				for(TableDistributeInfo info : distributeInfoList) {
					sqlExec = sqlExec.replaceAll("([\\s`])" + info.getTableName() + "([\\(\\s`])", 
							"$1" + info.getPhysicalTableMap().values().iterator().next()+ "$2");
					sqlExec = sqlExec.replaceAll("(\\s)" + info.getTableName() + "$", 
							"$1" + info.getPhysicalTableMap().values().iterator().next());
				}
				statement.setDbName(keyTable.getLogicDbMap().values().iterator().next());
			}
			int ret = this.executeUpdateInternal(statement.getDbName(), sqlExec, autoGeneratedKeys,columnIndexes,columnNames);
			if(log.isDebugEnabled()) {
				log.debug(ret + " records were updated.");
			}
			return ret;
		}else if(keyTable.getFullTableScan() > 1) {
			if(SQLType.INSERT.equals(sqlType)) {
				throw new SQLException("can't identify unique db to execute insert sql");
			}
			int ret = 0;
			boolean flag = false;
			ArrayList<ExecUpdateThread> tList = new ArrayList<ExecUpdateThread>();
			Vector<Integer> v = new Vector<Integer>();
			HashSet<Object> tables = new HashSet<Object>();
			for(String table : keyTable.getTableColValueMap().keySet()) {
				flag = false;
				String sqlExec = sql;
				int i = 0;
				for(TableDistributeInfo info : distributeInfoList) {
					if(i==0) {
						if(tables.contains(info.getPhysicalTableMap().get(keyTable.getTableColValueMap().get(table)))) {
							flag = true;
							break;
						}else {
							tables.add(info.getPhysicalTableMap().get(keyTable.getTableColValueMap().get(table)));
						}
					}
					i++;
					sqlExec = sqlExec.replaceAll("([\\s`])" + info.getTableName() + "([\\(\\s`])", 
							"$1" + info.getPhysicalTableMap().get(keyTable.getTableColValueMap().get(table))+ "$2");
					sqlExec = sqlExec.replaceAll("(\\s)" + info.getTableName() + "$", 
							"$1" + info.getPhysicalTableMap().get(keyTable.getTableColValueMap().get(table)));
					
				}
				if(flag) {
					continue;
				}
				statement.setDbName(keyTable.getLogicDbMap().get(keyTable.getTableColValueMap().get(table)));
				ExecUpdateThread t = new ExecUpdateThread(this, sqlExec, statement.getDbName(), v, autoGeneratedKeys,columnIndexes,columnNames);
				tList.add(t);
				t.start();
			}
			for(ExecUpdateThread t : tList) {
				try {
					t.join();
				} catch (InterruptedException e) {
				}
			}
			for(ExecUpdateThread t : tList) {
				if(!t.succeed) {
					throw new SQLException(t.getReason());
				}
			}
			for(Integer rs : v) {
				ret += rs;
			}
			return ret;
		}else if(keyTable.getFullTableScan() == -1) {
			if(SQLType.INSERT.equals(sqlType)) {
				throw new SQLException("can't identify unique db to execute insert sql");
			}
			int ret = 0;
			ArrayList<ExecUpdateThread> tList = new ArrayList<ExecUpdateThread>();
			Vector<Integer> v = new Vector<Integer>();
			DistributedTableRule tableRule = statement.getDbRule().getTableRules().get(keyTable.getTableName());
			JSONObject tableNameRule = JSONObject.fromObject(tableRule.getTableNameRule());
			if("Mapping".equals(tableNameRule.getString("operator"))) {
				Map<Object, Integer> tbMap = statement.getDbRule().getMappingRules().get(tableNameRule.getString("operatorValue"));
				if(tbMap == null) {
					throw new SQLException("invalid distribute rule");
				}
				Iterator<Object> key = tbMap.keySet().iterator();
				
				while(key.hasNext()) {
					String sqlExec = sql;
					Object keyValue = key.next();
					int tableId = DistributedDBRule.calculateDistributeId(tableNameRule, keyValue, statement.getDbRule().getMappingRules());
					for(TableDistributeInfo info : distributeInfoList) {
						sqlExec = sqlExec.replaceAll("([\\s`])" + info.getTableName() + "([\\(\\s`])", 
								"$1" + DistributedDBRule.calculatePattern(tableId, keyTable.getTablePattern()) + "$2");
						sqlExec = sqlExec.replaceAll("(\\s)" + info.getTableName() + "$", 
								"$1" + DistributedDBRule.calculatePattern(tableId, keyTable.getTablePattern()));
					}
					int dbId = DistributedDBRule.calculateDistributeId(JSONObject.fromObject(tableRule.getLogicDBRule()), keyValue, statement.getDbRule().getMappingRules());
					statement.setDbName(DistributedDBRule.calculatePattern(dbId, keyTable.getDbPattern()));
					ExecUpdateThread t = new ExecUpdateThread(this, sqlExec, statement.getDbName(), v, autoGeneratedKeys,columnIndexes,columnNames);
					tList.add(t);
					t.start();
				}
				
			}else {
				for(int i=0; i<tableRule.getSubTableCount(); i++) {
					String sqlExec = sql;
					int tableId = DistributedDBRule.calculateDistributeId(tableNameRule, i, statement.getDbRule().getMappingRules());
					for(TableDistributeInfo info : distributeInfoList) {
						sqlExec = sqlExec.replaceAll("([\\s`])" + info.getTableName() + "([\\(\\s`])", 
								"$1" + DistributedDBRule.calculatePattern(tableId, keyTable.getTablePattern()) + "$2");
						sqlExec = sqlExec.replaceAll("(\\s)" + info.getTableName() + "$", 
								"$1" + DistributedDBRule.calculatePattern(tableId, keyTable.getTablePattern()));
					}
					int dbId = DistributedDBRule.calculateDistributeId(JSONObject.fromObject(tableRule.getLogicDBRule()), i, statement.getDbRule().getMappingRules());
					statement.setDbName(DistributedDBRule.calculatePattern(dbId, keyTable.getDbPattern()));
					ExecUpdateThread t = new ExecUpdateThread(this, sqlExec, statement.getDbName(), v, autoGeneratedKeys,columnIndexes,columnNames);
					tList.add(t);
					t.start();
				}
			}
			for(Thread t : tList) {
				try {
					t.join();
				} catch (InterruptedException e) {
				}
			}
			for(ExecUpdateThread t : tList) {
				if(!t.succeed) {
					throw new SQLException(t.getReason());
				}
			}
			for(Integer rs : v) {
				ret += rs;
			}
			return ret;
		}else {
			throw new SQLException("invalid fullTableScan value:" + keyTable.getFullTableScan());
		}
	}
	
	ResultSet executeQueryInternal(String dbName, String sqlExec) throws SQLException {
		Connection conn = statement.getRealConnection(dbName);
//		LogicDBConnection conn = (LogicDBConnection) statement.getRealConnection(dbName);
		if(prepared) {
			if(log.isDebugEnabled()) {
				log.debug("sql:" + getSql());
				log.debug(this.valueMap);
			}
			DistributedPreparedStatement dps = (DistributedPreparedStatement)statement;
			PreparedStatement ps = null;
//			LogicDBPreparedStatement ps = null;

			if (dps.getAutoGeneratedKeys() != -1) {
				ps = conn.prepareStatement(sqlExec, dps.getAutoGeneratedKeys());
			} else if (dps.getColumnIndexes() != null && dps.getColumnIndexes().length > 0) {
				ps = conn.prepareStatement(sqlExec, dps.getColumnIndexes());
			} else if (dps.getColumnNames() != null && dps.getColumnNames().length > 0) {
				ps = conn.prepareStatement(sqlExec, dps.getColumnNames());
			} else if (dps.getResultSetType() != -1 && dps.getResultSetConcurrency() != -1 && dps.getResultSetHoldability() != -1) {
				ps = conn.prepareStatement(sqlExec, dps.getResultSetType(), dps.getResultSetConcurrency(), dps.getResultSetHoldability());
			} else if (dps.getResultSetType() != -1 && dps.getResultSetConcurrency() != -1) {
				ps = conn.prepareStatement(sqlExec, dps.getResultSetType(), dps.getResultSetConcurrency());
			} else {
				ps = conn.prepareStatement(sqlExec);
			}
			manager.addStatement(ps);
			for (Integer key : this.valueMap.keySet()) {
				ps.setObject(key, this.valueMap.get(key));
			}

			return ((LogicDBPreparedStatement) ps).executeQuery(Constants.DISTRIBUTED_DB_FLAG);

		}else {
			Statement state = conn.createStatement();
			manager.addStatement(state);
			return ((LogicDBStatement) state).executeQuery(sqlExec, Constants.DISTRIBUTED_DB_FLAG);
		}
	}
	
	private int executeUpdateInternal(String dbName, String sqlExec, int autoGeneratedKeys, int[] columnIndexes, 
			String[] columnNames) throws SQLException {
		Connection conn = statement.getRealConnection(dbName);
		if(prepared) {
			if(log.isDebugEnabled()) {
				log.debug("sql:" + getSql());
				log.debug(this.valueMap);
			}
			DistributedPreparedStatement dps = (DistributedPreparedStatement)statement;
			PreparedStatement ps = null;
			try {
				if (dps.getAutoGeneratedKeys() != -1) {
					ps = conn.prepareStatement(sqlExec, dps.getAutoGeneratedKeys());
				} else if (dps.getColumnIndexes() != null && dps.getColumnIndexes().length > 0) {
					ps = conn.prepareStatement(sqlExec, dps.getColumnIndexes());
				} else if (dps.getColumnNames() != null && dps.getColumnNames().length > 0) {
					ps = conn.prepareStatement(sqlExec, dps.getColumnNames());
				} else if (dps.getResultSetType() != -1 && dps.getResultSetConcurrency() != -1 && dps.getResultSetHoldability() != -1) {
					ps = conn.prepareStatement(sqlExec, dps.getResultSetType(), dps.getResultSetConcurrency(), dps.getResultSetHoldability());
				} else if (dps.getResultSetType() != -1 && dps.getResultSetConcurrency() != -1) {
					ps = conn.prepareStatement(sqlExec, dps.getResultSetType(), dps.getResultSetConcurrency());
				} else {
					ps = conn.prepareStatement(sqlExec);
				}
				manager.addStatement(ps);
				for (Integer key : this.valueMap.keySet()) {
					Integer type = this.typeMap.get(key);
					if (type.intValue() == Types.BINARY) {
						@SuppressWarnings("unchecked")
						Map<String, Object> valueMap = (Map<String, Object>) this.valueMap.get(key);
						Object len = valueMap.get("length");
						if (null == len) {
							ps.setBinaryStream(key, (InputStream) valueMap.get("value"));
						} else {
							ps.setBinaryStream(key, (InputStream) valueMap.get("value"), (Long) len);
						}

					} else if (type.intValue() == Types.NCLOB) {
						@SuppressWarnings("unchecked")
						Map<String, Object> valueMap = (Map<String, Object>) this.valueMap.get(key);
						Object len = valueMap.get("length");
						if (null == len) {
							ps.setClob(key, (Reader) valueMap.get("value"));
						} else {
							ps.setClob(key, (Reader) valueMap.get("value"), (Long) len);
						}

					} else {
						ps.setObject(key, this.valueMap.get(key));
					}
//				ps.setObject(key, this.valueMap.get(key));
				}
				return ps.executeUpdate();
			} finally {
				if (null != ps) {
					ps.close();
					ps = null;
				}
			}
		}else {
			try (Statement state = conn.createStatement()) {
				manager.addStatement(state);
				if (autoGeneratedKeys != -1) {
					return state.executeUpdate(sqlExec, autoGeneratedKeys);
				} else if (columnIndexes != null) {
					return state.executeUpdate(sqlExec, columnIndexes);
				} else if (columnNames != null) {
					return state.executeUpdate(sqlExec, columnNames);
				} else {
					return state.executeUpdate(sqlExec);
				}
			}
		}
	}
	
	public DistributedStatement getStatement() {
		return statement;
	}

	public void setStatement(DistributedStatement statement) {
		this.statement = statement;
	}

	public boolean isPrepared() {
		return prepared;
	}

	public void setPrepared(boolean prepared) {
		this.prepared = prepared;
	}

	public ConnectionManager getManager() {
		return manager;
	}
	public void setManager(ConnectionManager manager) {
		this.manager = manager;
	}
	public List<TableDistributeInfo> getDistributeInfoList() {
		return distributeInfoList;
	}
	public void setDistributeInfoList(List<TableDistributeInfo> distributeInfoList) {
		this.distributeInfoList = distributeInfoList;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public Map<Integer, Object> getValueMap() {
		return valueMap;
	}
	public void setValueMap(Map<Integer, Object> valueMap) {
		this.valueMap = valueMap;
	}
	

	public Map<Integer, Integer> getTypeMap() {
        return typeMap;
    }

    public void setTypeMap(Map<Integer, Integer> typeMap) {
        this.typeMap = typeMap;
    }

    public ParsedSqlContext getCtx() {
		return ctx;
	}

	public void setCtx(ParsedSqlContext ctx) {
		this.ctx = ctx;
	}
	
	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	class ExecQueryThread extends Thread {
		private SqlExecPlan execPlan;
		private String sql;
		private String dbName;
		private Vector<ResultSet> v;
		private boolean succeed = true;
		private SQLException reason;
		
		public ExecQueryThread(SqlExecPlan execPlan,String sql,String dbName, Vector<ResultSet> v) {
			this.execPlan = execPlan;
			this.sql = sql;
			this.dbName = dbName;
			this.v = v;
		}
		
		public void run() {
			try {
				ResultSet rs = execPlan.executeQueryInternal(dbName, sql);
				v.add(rs);
			} catch (SQLException e) {
				succeed = false;
				reason = e;
				log.error("SQLException:" + e);
				e.printStackTrace();
			}
		}

		public boolean isSucceed() {
			return succeed;
		}

		public void setSucceed(boolean succeed) {
			this.succeed = succeed;
		}

		public SQLException getReason() {
			return reason;
		}

		public void setReason(SQLException reason) {
			this.reason = reason;
		}
	
	}
	
	class ExecUpdateThread extends Thread {
		private SqlExecPlan execPlan;
		private String sql;
		private String dbName;
		private Vector<Integer> v;
		private int autoGeneratedKeys;
		private int[] columnIndexes;
		private String[] columnNames;
		private boolean succeed = true;
		private SQLException reason;
		
		public ExecUpdateThread(SqlExecPlan execPlan,String sql,String dbName, Vector<Integer> v,int autoGeneratedKeys, int[] columnIndexes, 
				String[] columnNames) {
			this.execPlan = execPlan;
			this.sql = sql;
			this.dbName = dbName;
			this.v = v;
			this.autoGeneratedKeys = autoGeneratedKeys;
			this.columnIndexes = columnIndexes;
			this.columnNames = columnNames;
		}
		
		public void run() {
			try {
				int ret = execPlan.executeUpdateInternal(dbName, sql, autoGeneratedKeys,columnIndexes,columnNames);;
				v.add(ret);
			} catch (SQLException e) {
				succeed = false;
				reason = e;
				log.error("SQLException:" + e);
				e.printStackTrace();
			}
		}
		public boolean isSucceed() {
			return succeed;
		}

		public void setSucceed(boolean succeed) {
			this.succeed = succeed;
		}

		public SQLException getReason() {
			return reason;
		}

		public void setReason(SQLException reason) {
			this.reason = reason;
		}
	}
	
}
