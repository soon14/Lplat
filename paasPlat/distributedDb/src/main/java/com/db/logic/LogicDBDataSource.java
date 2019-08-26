package com.db.logic;

import com.zengshi.paas.ConfigurationCenter;
import com.zengshi.paas.ConfigurationWatcher;
import com.zengshi.paas.PaasException;
import com.zengshi.paas.utils.CipherUtil;
import com.zengshi.paas.utils.StringUtil;
import com.db.common.DistributedTransactionManager;
import com.db.util.Constants;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
//import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Random;

/**
 * 逻辑库的DataSource类
 * @date 2014年6月23日 上午9:58:04 
 * @version V1.0
 */
public class LogicDBDataSource implements DataSource, ConfigurationWatcher {
	public static final Logger log = Logger.getLogger(LogicDBDataSource.class);
	private String logicDB;
	private String dbType;
	private JSONObject dbRule;
	private HashMap<String, DataSource> dbMap = new HashMap<String, DataSource>();
	private HashMap<String, Integer> weightMap = new HashMap<String, Integer>();
	private int weight;
	private Random random = null;
	
	
	private String confPath = "/com/zpaas/db/logicDb/conf";
	private String dbConf = null;
	private ConfigurationCenter cc = null;

	public void init() {
		try {
			process(cc.getConfAndWatch(confPath, this));
			
		} catch (PaasException e) {
			log.error(e.getMessage(),e);
		}
	}
	
	public void process(String conf) {
		if(log.isInfoEnabled()) {
			log.info("new LogicDBDataSource configuration is received: " + conf);
		}
		if(conf == null || conf.trim().length() == 0) {
			log.error("LogicDBDataSource configuration is empty.");
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
			log.error(e.getMessage(),e);
			return;
		}

		if(dbRule != null) {
			logicDB = dbRule.getString("logicDB");
			if(logicDB == null || logicDB.trim().length() == 0) {
				log.error("invalid db conf: logicDB is empty.");
				return;
			}
			if(log.isDebugEnabled()) {
				log.debug("init LogicDBDataSource:" + logicDB);
				log.debug("----dbRule:" + dbRule);
			}
			String dbName = dbRule.getString("master");
			if(dbName == null || dbName.trim().length() == 0) {
				log.error("invalid db conf: master is empty.");
				return;
			}
			if(log.isDebugEnabled()) {
				log.debug("create master DataSource:" + dbName);
			}

			JSONObject conf = dbRule.getJSONObject(dbName);
//			BasicDataSource ds = new BasicDataSource();
			ExtBasicDataSource ds = new ExtBasicDataSource();
			if(conf.containsKey("driver")) {
				String driver = conf.getString("driver");
				if(driver != null && driver.trim().length() > 0) {
					ds.setDriverClassName(driver);
				}else {
					log.error("invalid db conf of " + dbName + ": driver is empty.");
				}
			}else {
				log.error("invalid db conf of " + dbName + ": driver is empty.");
			}
			String username = "";
			if(conf.containsKey("username")) {
				username = conf.getString("username");
				if(username != null && username.trim().length() >0) {
					ds.setUsername(username);
				}else {
					log.error("invalid db conf of " + dbName + ": username is empty.");
				}
			}else {
				log.error("invalid db conf of " + dbName + ": username is empty.");
			}
			if(conf.containsKey("password")) {
				String password = null;
				try {
					password = CipherUtil.decrypt(conf.getString("password"));
				} catch (Exception e) {
					log.error("invalid password:" + e);
					e.printStackTrace();
				}
				if(password != null && password.trim().length() > 0) {
					ds.setPassword(password);
				}else {
					log.error("invalid db conf of " + dbName + ": password is empty.");
				}
			}else {
				log.error("invalid db conf of " + dbName + ": password is empty.");
			}
            String monUrl = "";
			if(conf.containsKey("url")) {
                String url = conf.getString("url");
				if(url != null && url.trim().length() > 0) {
					ds.setUrl(url);
                    monUrl = url;
				}else {
					log.error("invalid db conf of " + dbName + ": url is empty.");
				}
			}else {
				log.error("invalid db conf of " + dbName + ": url is empty.");
			}
            //开启数据库JMX监控
            setDbMonJmxInfo(ds, username, monUrl);
            if(conf.containsKey("minIdle")) {
				ds.setMinIdle(conf.getInt("minIdle"));
			}
			else {
				ds.setMinIdle(3);
			}

			if(conf.containsKey("initSize")) {
				ds.setInitialSize(conf.getInt("initSize"));
			}
			else {
				ds.setInitialSize(5);
			}
			if(conf.containsKey("maxActive")) {
//				ds.setMaxActive(conf.getInt("maxActive"));
				ds.setMaxTotal(conf.getInt("maxActive"));
			}
			if(conf.containsKey("maxIdle")) {
				ds.setMaxIdle(conf.getInt("maxIdle"));
			}
			if(conf.containsKey("maxWait")) {
//				ds.setMaxWait(conf.getInt("maxWait"));
				ds.setMaxWaitMillis(conf.getLong("maxWait"));
			}
			if(conf.containsKey("validationQuery")) {
				ds.setValidationQuery(conf.getString("validationQuery").replace("&nbsp;", " "));
			}
			if(conf.containsKey("testWhileIdle")) {
				ds.setTestWhileIdle(Boolean.valueOf(conf.getString("testWhileIdle")));
			}
			int weight = dbRule.getInt("weight");
			weightMap.put(dbName+"_min", 0);
			weightMap.put(dbName+"_max", weight);
			ds.initDataSource();
			dbMap.put(dbName, ds);
			JSONArray array = dbRule.getJSONArray("slaves");
			if(array != null && array.size() > 0) {
				for(int i=0; i<array.size(); i++) {
					dbName = array.getJSONObject(i).getString("slave");
					conf = dbRule.getJSONObject(dbName);
					if(log.isDebugEnabled()) {
						log.debug("create slave DataSource:" + dbName);
						log.debug("slave DataSource:" + conf);
					}
//					ds = new BasicDataSource();
					ds = new ExtBasicDataSource();
					if(conf.containsKey("driver")) {
						String driver = conf.getString("driver");
						if(driver != null && driver.trim().length() > 0) {
							ds.setDriverClassName(driver);
						}else {
							log.error("invalid db conf of " + dbName + ": driver is empty.");
						}
					}else {
						log.error("invalid db conf of " + dbName + ": driver is empty.");
					}

					if(conf.containsKey("username")) {
						username = conf.getString("username");
						if(username != null && username.trim().length() >0) {
							ds.setUsername(username);
						}else {
							log.error("invalid db conf of " + dbName + ": username is empty.");
						}
					}else {
						log.error("invalid db conf of " + dbName + ": username is empty.");
					}
					if(conf.containsKey("password")) {
						String password = null;
						try {
							password = CipherUtil.decrypt(conf.getString("password"));
						} catch (Exception e) {
							log.error("invalid password:" + e);
							e.printStackTrace();
						}
						if(password != null && password.trim().length() > 0) {
							ds.setPassword(password);
						}else {
							log.error("invalid db conf of " + dbName + ": password is empty.");
						}
					}else {
						log.error("invalid db conf of " + dbName + ": password is empty.");
					}
                    monUrl = "";
					if(conf.containsKey("url")) {
						String url = conf.getString("url");
						if(url != null && url.trim().length() > 0) {
							ds.setUrl(url);
                            monUrl = url;
						}else {
							log.error("invalid db conf of " + dbName + ": url is empty.");
						}
					}else {
						log.error("invalid db conf of " + dbName + ": url is empty.");
					}
                    //开启数据库JMX监控
                    setDbMonJmxInfo(ds, username, monUrl);
//					ds.setJmxName(Constants.JMX_OBJECT_NAME+username);
					if(conf.containsKey("minIdle")) {
						ds.setMinIdle(conf.getInt("minIdle"));
					}
					else {
						ds.setMinIdle(3);
					}

					if(conf.containsKey("initSize")) {
						ds.setInitialSize(conf.getInt("initSize"));
					}
					else {
						ds.setInitialSize(5);
					}
					if(conf.containsKey("maxActive")) {
//						ds.setMaxActive(conf.getInt("maxActive"));
						ds.setMaxWaitMillis(conf.getLong("maxWait"));
					}
					if(conf.containsKey("maxIdle")) {
						ds.setMaxIdle(conf.getInt("maxIdle"));
					}
					if(conf.containsKey("maxWait")) {
//						ds.setMaxWait(conf.getInt("maxWait"));
						ds.setMaxWaitMillis(conf.getLong("maxWait"));
					}
					if(conf.containsKey("validationQuery")) {
						ds.setValidationQuery(conf.getString("validationQuery").replace("&nbsp;", " "));
					}
					if(conf.containsKey("testWhileIdle")) {
						ds.setTestWhileIdle(Boolean.valueOf(conf.getString("testWhileIdle")));
					}
					weightMap.put(dbName+"_min", weight);
					weight += array.getJSONObject(i).getInt("weight");
					weightMap.put(dbName+"_max", weight);
					ds.initDataSource();
					dbMap.put(dbName, ds);
				}
			}

			this.weight = weight;
			random = new Random(weight);
		}else {
			log.error("dbRule is null");
		}
	}

    private void setDbMonJmxInfo(ExtBasicDataSource ds, String username,
                                 String url) {
        try {
            String dbInfo = "";
            if (StringUtil.isNotBlank(url)) {
                String delStr = "@|//|\\?";
                String[] urlFlds = url.split(delStr);
                dbInfo = urlFlds[1].replace(":", "_").replace(".", "_").replace("/", "_");
            }
            ds.setJmxName(Constants.JMX_OBJECT_NAME + dbInfo + "_" + username);
        }catch (Exception e) {
            log.warn("设置JMX数据库监控的异常:",e);
        }
    }

    public int randomDbIndex() {
		return random.nextInt(weight);
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
	public int getLoginTimeout() throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		throw new SQLException("unsupported operation");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		try {
			return (T) this;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException(e);
		}
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		if (super.getClass().isAssignableFrom(iface)) {
			return true;
		}
		return false;
	}

	@Override
	public Connection getConnection() throws SQLException {
		LogicDBConnection conn = new LogicDBConnection();
		conn.setDbRule(dbRule);
		conn.setDs(this);
//		String dbName=selectDB();
//		if(StringUtils.hasText(dbName)){
//		    conn.setWrappedConnection(this.dbMap.get(dbName).getConnection());
//		}

		if(DistributedTransactionManager.isInTransaction()) {
			conn.setAutoCommit(false);
			if(!DistributedTransactionManager.isDistributedLevel()) {
				DistributedTransactionManager.bindConnection(logicDB, conn);
			}
		}
		return conn;
	}

	protected String selectDB(){
	    int dbIndex = this.randomDbIndex();
        for(String dbName : this.getDbMap().keySet()) {
			if (dbIndex >= this.getWeightMap().get(dbName + "_min")
					&& dbIndex < this.getWeightMap().get(dbName+"_max")) {
                return dbName;
            }
        }
        return null;
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		return this.getConnection();
	}

	public String getLogicDB() {
		return logicDB;
	}

	public void setLogicDB(String logicDB) {
		this.logicDB = logicDB;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
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

	public HashMap<String, DataSource> getDbMap() {
		return dbMap;
	}

	public void setDbMap(HashMap<String, DataSource> dbMap) {
		this.dbMap = dbMap;
	}

	public HashMap<String, Integer> getWeightMap() {
		return weightMap;
	}

	public void setWeightMap(HashMap<String, Integer> weightMap) {
		this.weightMap = weightMap;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	private class ExtBasicDataSource extends BasicDataSource {
		@Override
		public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
			return null;
		}

		public void initDataSource() {
			try {
				createDataSource();
			} catch (SQLException e) {
				log.error("create DataSource ex:" + e);
				log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
	}

}
