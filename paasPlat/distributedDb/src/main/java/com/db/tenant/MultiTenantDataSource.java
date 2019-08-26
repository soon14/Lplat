package com.db.tenant;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.drools.core.util.FileManager;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ByteArrayResource;

import com.zengshi.paas.ConfigurationCenter;
import com.zengshi.paas.ConfigurationWatcher;
import com.zengshi.paas.PaasException;
import com.zengshi.paas.file.FileManagerSVC;
import com.db.distribute.DistributedDataSource;

import net.sf.json.JSONObject;

/**
 * 多租户库的DataSource类
 * @date 2014年6月23日 上午9:58:04 
 * @version V1.0
 */
public class MultiTenantDataSource implements DataSource, ConfigurationWatcher {
	public static final Logger log = Logger.getLogger(MultiTenantDataSource.class);
	private String multiTenantDB;
	private String sqlType;
	private JSONObject dbRule;
	private HashMap<String, DistributedDataSource> dbMap = new HashMap<String, DistributedDataSource>();
	
	
	private String confPath = "/com/zpaas/db/multiTenantDb/conf";
	private String dbConf = null;
	private ConfigurationCenter cc = null;
	private FileManagerSVC fileManager = null;
	
	public void init() {
		try {
			process(cc.getConfAndWatch(confPath, this));
			
		} catch (PaasException e) {
			e.printStackTrace();
		}
	}
	
	public void process(String conf) {
		if(log.isInfoEnabled()) {
			log.info("new MultiTenantDataSource configuration is received: " + conf);
		}
		if(conf == null || conf.trim().length() == 0) {
			log.error("MultiTenantDataSource configuration is empty.");
			return;
		}
		if(conf != null && !conf.equals(dbConf)) {
			dbConf = conf;
			initDb();
		}
	}
	
	public void initDb() {
		try {
			dbRule = JSONObject.fromObject(dbConf);
		} catch (Exception e) {
			log.error("invalid json format:" + e + " dbConf is:" + dbConf);
			e.printStackTrace();
			return;
		}
		
		if(dbRule != null) {
			multiTenantDB = dbRule.getString("multiTenantDB");
			if(multiTenantDB == null || multiTenantDB.trim().length() == 0) {
				log.error("invalid db conf: logicDB is empty.");
				return;
			}
			if(log.isDebugEnabled()) {
				log.debug("init MultiTenantDataSource:" + multiTenantDB);
				log.debug("----dbRule:" + dbRule);
			}
			JSONObject distributedDBs = dbRule.getJSONObject("distributedDBs");
			if(distributedDBs == null || distributedDBs.size() == 0) {
				log.error("invalid db conf: distributedDBs is empty.");
				return;
			}
			if(log.isDebugEnabled()) {
				log.debug("create distributedDB DataSources");
			}
			
			String filePath = dbRule.getString("fileId");
			if(filePath == null || filePath.trim().length() == 0) {
				log.error("invalid db conf: fileId is empty.");
				return;
			}
			XmlBeanFactory ctx = null;
			try {
				ByteArrayResource res = new ByteArrayResource(fileManager.readFile(filePath));
				ctx = new XmlBeanFactory(res);
			} catch (Exception e) {
				log.error("load db rule from file failed:" + e);
				log.error(e.getMessage(),e);
				return;
			}
			
			@SuppressWarnings("unchecked")
			Iterator<String> keys = (Iterator<String>)distributedDBs.keys();
			while(keys.hasNext()) {
				String tenantId = keys.next();
				String dbName = distributedDBs.getString(tenantId);
				DistributedDataSource ds = (DistributedDataSource)ctx.getBean(dbName);
				if(ds == null) {
					log.error("init DistributedDataSource failed:" + dbName);
				}
				dbMap.put(tenantId, ds);
			}
		}else {
			log.error("dbRule is null");
		}
	}
	
	
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		try {
			return (T) this;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new SQLException(e);
		}
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		if(super.getClass().isAssignableFrom(iface)) {
			return true;
		}
		return false;
	}

	@Override
	public Connection getConnection() throws SQLException {
		MultiTenantConnection conn = new MultiTenantConnection();
		conn.setDbRule(dbRule);
		conn.setDs(this);
		return conn;
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		return this.getConnection();
	}

	public String getSqlType() {
		return sqlType;
	}

	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}

	public JSONObject getDbRule() {
		return dbRule;
	}

	public void setDbRule(JSONObject dbRule) {
		this.dbRule = dbRule;
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

	public HashMap<String, DistributedDataSource> getDbMap() {
		return dbMap;
	}

	public void setDbMap(HashMap<String, DistributedDataSource> dbMap) {
		this.dbMap = dbMap;
	}

	

	public FileManagerSVC getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileManagerSVC fileManager) {
		this.fileManager = fileManager;
	}

	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

}
