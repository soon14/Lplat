package com.db.distribute;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.db.sql.ParsedSqlContext;
import com.db.sql.SQLType;
import com.db.sql.TableDistributeInfo;

/**
 * 分布式数据库的PreparedStatement类
 * @date 2014年6月23日 上午9:55:40 
 * @version V1.0
 */
public class DistributedPreparedStatement extends DistributedStatement
				implements PreparedStatement {
	public static final Logger log = Logger.getLogger(DistributedPreparedStatement.class);
	
	private Map<Integer, Integer> typeMap = new HashMap<Integer,Integer>();
	private Map<Integer,Object> valueMap = new HashMap<Integer,Object>();
	private String sql;
	private int autoGeneratedKeys = -1;
	private int[] columnIndexes;
	private String[] columnNames;
	private ParsedSqlContext ctx = null;
	
	
	
	public DistributedPreparedStatement(String sql) {
		this.setSql(sql.toLowerCase());
		if(log.isDebugEnabled()) {
			log.debug(this.getSql());
		}		
	}
	
	protected void processDbRule(String sql) throws SQLException{
		ctx = getDbRule().getParsedSqlContext(sql, valueMap);
		if(ctx == null) {
			throw new SQLException("getParsedSqlContext failed");
		}
		List<TableDistributeInfo> ret = getDbRule().parseSql(sql, valueMap, ctx);
		if(ret != null) {
			plan = new SqlExecPlan();
			plan.setDistributeInfoList(ret);
			plan.setManager(getManager());
			plan.setPrepared(true);
			plan.setSql(sql);
			plan.setStatement(this);
			plan.setValueMap(valueMap);
			plan.setTypeMap(typeMap);
			plan.setCtx(ctx);
			plan.setDbType(this.getDbRule().getDbType());
		}
	}

	@Override
	public boolean execute() throws SQLException {
		String sqlType = SQLType.getSqlType(sql);
		if(SQLType.SELECT.equals(sqlType) || SQLType.DESC.equals(sqlType) || SQLType.SHOW.equals(sqlType)) {
			this.setResultSet(executeQuery());
			return true;
		}else{
			this.updateCount = executeUpdate();
			return true;
		}
	}

	@Override
	public void addBatch() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int[] executeBatch() throws SQLException {
		// TODO Auto-generated method stub
		return super.executeBatch();
	}
	
	@Override
	public ResultSet executeQuery() throws SQLException {
		this.processDbRule(sql);
		long beginTime = System.currentTimeMillis();
		ResultSet rs = plan.executeQuery();
		long endTime = System.currentTimeMillis();
		if(log.isDebugEnabled()) {
			log.debug("distributedDb(" + getDbName() + ") cost " + (endTime-beginTime) + " ms to execute sql: " + sql);
			log.debug(this.valueMap);
		}
		return rs;
	}
	
	@Override
	public int executeUpdate() throws SQLException {
		this.processDbRule(sql);
		long beginTime = System.currentTimeMillis();
		int result = plan.executeUpdate(-1,null,null);
		long endTime = System.currentTimeMillis();
		if(log.isDebugEnabled()) {
			log.debug("distributedDb(" + getDbName() + ") cost " + (endTime-beginTime) + " ms to execute sql: " + sql);
			log.debug(this.valueMap);
		}
		return result;
	}
	
	@Override
	public void setObject(int parameterIndex, Object x) throws SQLException {
		typeMap.put(parameterIndex, Types.OTHER);
		valueMap.put(parameterIndex, x);
	}
	
	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		typeMap.put(parameterIndex, Types.ROWID);
		valueMap.put(parameterIndex, x);
	}

	@Override
	public void setNString(int parameterIndex, String value)
			throws SQLException {
		typeMap.put(parameterIndex, Types.NVARCHAR);
		valueMap.put(parameterIndex, value);
	}

	@Override
	public void setRef(int parameterIndex, Ref x) throws SQLException {
		typeMap.put(parameterIndex, Types.REF);
		valueMap.put(parameterIndex, x);
	}

	@Override
	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		typeMap.put(parameterIndex, Types.BLOB);
		valueMap.put(parameterIndex, x);
	}

	@Override
	public void setClob(int parameterIndex, Clob x) throws SQLException {
		typeMap.put(parameterIndex, Types.CLOB);
		valueMap.put(parameterIndex, x);
	}

	@Override
	public void setArray(int parameterIndex, Array x) throws SQLException {
		typeMap.put(parameterIndex, Types.ARRAY);
		valueMap.put(parameterIndex, x);
	}
	
	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		typeMap.put(parameterIndex, Types.NULL);
		valueMap.put(parameterIndex, null);
	}

	@Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		typeMap.put(parameterIndex, Types.BOOLEAN);
		valueMap.put(parameterIndex, x);
	}

	@Override
	public void setByte(int parameterIndex, byte x) throws SQLException {
		typeMap.put(parameterIndex, Types.TINYINT);
		valueMap.put(parameterIndex, x);
	}

	@Override
	public void setShort(int parameterIndex, short x) throws SQLException {
		typeMap.put(parameterIndex, Types.SMALLINT);
		valueMap.put(parameterIndex, x);
	}

	

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x)
			throws SQLException {
		typeMap.put(parameterIndex, Types.TIMESTAMP);
		valueMap.put(parameterIndex, x);
	}
	
	@Override
	public void setInt(int parameterIndex, int x) throws SQLException {
		typeMap.put(parameterIndex, Types.INTEGER);
		valueMap.put(parameterIndex, x);
	}

	@Override
	public void setLong(int parameterIndex, long x) throws SQLException {
		typeMap.put(parameterIndex, Types.BIGINT);
		valueMap.put(parameterIndex, x);
	}

	@Override
	public void setFloat(int parameterIndex, float x) throws SQLException {
		typeMap.put(parameterIndex, Types.FLOAT);
		valueMap.put(parameterIndex, x);
	}

	@Override
	public void setDouble(int parameterIndex, double x) throws SQLException {
		typeMap.put(parameterIndex, Types.DOUBLE);
		valueMap.put(parameterIndex, x);
	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x)
			throws SQLException {
		typeMap.put(parameterIndex, Types.DECIMAL);
		valueMap.put(parameterIndex, x);
	}

	@Override
	public void setString(int parameterIndex, String x) throws SQLException {
		typeMap.put(parameterIndex, Types.VARCHAR);
		valueMap.put(parameterIndex, x);
	}

	@Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		typeMap.put(parameterIndex, Types.CLOB);
		valueMap.put(parameterIndex, x);
	}

	@Override
	public void setDate(int parameterIndex, Date x) throws SQLException {
		typeMap.put(parameterIndex, Types.DATE);
		valueMap.put(parameterIndex, x);
	}

	@Override
	public void setTime(int parameterIndex, Time x) throws SQLException {
		typeMap.put(parameterIndex, Types.TIME);
		valueMap.put(parameterIndex, x);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@Override
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
	    setBinaryStream(parameterIndex, x, Long.valueOf(length));
	}

	@Override
	public void clearParameters() throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		throw new SQLException("unsupported operation");
	}

	

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
	    setCharacterStream(parameterIndex,reader,Long.valueOf(length));
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@Override
	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@Override
	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@Override
	public void setURL(int parameterIndex, URL x) throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		typeMap.put(parameterIndex, Types.NCHAR);
		valueMap.put(parameterIndex, value);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
	    setCharacterStream(parameterIndex,reader,length);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
	    setBinaryStream(parameterIndex,inputStream,length);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
	    this.typeMap.put(parameterIndex, Types.BINARY);
        Map<String,Object> valueMap=new HashMap<String,Object>();
        valueMap.put("value", x);
        valueMap.put("length", length);
        this.valueMap.put(parameterIndex, valueMap);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
	    this.typeMap.put(parameterIndex, Types.NCLOB);
        Map<String,Object> valueMap=new HashMap<String,Object>();
        valueMap.put("value", reader);
        valueMap.put("length", length);
        this.valueMap.put(parameterIndex, valueMap);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
	    this.typeMap.put(parameterIndex, Types.BINARY);
        Map<String,Object> valueMap=new HashMap<String,Object>();
        valueMap.put("value", x);
        this.valueMap.put(parameterIndex, valueMap);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
	    this.typeMap.put(parameterIndex, Types.NCLOB);
        Map<String,Object> valueMap=new HashMap<String,Object>();
        valueMap.put("value", reader);
        this.valueMap.put(parameterIndex, valueMap);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
	    setCharacterStream(parameterIndex,reader);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
	    setBinaryStream(parameterIndex,inputStream);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		throw new SQLException("unsupported operation");
	}
	
	protected String getSql() {
		return sql;
	}

	protected void setSql(String sql) {
		this.sql = sql.toLowerCase();
	}

	public int getAutoGeneratedKeys() {
		return autoGeneratedKeys;
	}

	public void setAutoGeneratedKeys(int autoGeneratedKeys) {
		this.autoGeneratedKeys = autoGeneratedKeys;
	}

	public int[] getColumnIndexes() {
		return columnIndexes;
	}

	public void setColumnIndexes(int[] columnIndexes) {
		this.columnIndexes = columnIndexes;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}
}
