/** 
 * Date:2015年7月31日上午10:18:09 
 * 
*/
package com.zengshi.ecp.frame.sequence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

//import org.apache.commons.dbcp.BasicDataSource;
import com.zengshi.paas.utils.StringUtil;
import com.db.util.Constants;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;

import com.zengshi.paas.ConfigurationCenter;
import com.zengshi.paas.ConfigurationWatcher;
import com.zengshi.paas.PaasException;
import com.zengshi.paas.utils.CipherUtil;
import com.db.sequence.SequenceCache;
import com.db.sequence.SequenceServiceImpl;

import net.sf.json.JSONObject;

/** 
 * Description: <br>
 * Date:2015年7月31日上午10:18:09  <br>
 * 
 * @since JDK 1.6 
 * @see       
 */
public class PaasSequenceServiceImpl implements ConfigurationWatcher,PassSequenceService{

    public static final Logger log = Logger.getLogger(SequenceServiceImpl.class);
    
    private int range = 1000;
    private String sqlSelect;
    private String sqlUpdate;
    private String sequenceTable = "sequence";
    
    private DataSource db;
    private JSONObject conf;
    private String dbConf = null;
    
    
    
    private String confPath = "/com/zengshi/paas/db/sequence/conf";  
    private ConfigurationCenter cc = null;
    
    public SequenceCache getSequenceCache(String sequenceName){
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);
            if(sqlSelect  == null || sqlSelect.length() == 0) {
                sqlSelect = "select value from " + sequenceTable + " where name = ? for update";
            }
            ps = conn.prepareStatement(sqlSelect);
            ps.setString(1, sequenceName);
            rs = ps.executeQuery();
            if(rs != null && rs.next()) {
                long currVal = rs.getLong(1);
                try {
                    rs.close();
                } catch (Exception e) {
                    log.error("序列["+sequenceName+"]读取错误",e);
                }
                try {
                    ps.close();
                } catch (Exception e) {
                    log.error("序列["+sequenceName+"]读取错误",e);
                }
                if(sqlUpdate  == null || sqlUpdate.length() == 0) {
                    sqlUpdate = "update " + sequenceTable + " set value= value+ ? where name = ?";
                }
                ps = conn.prepareStatement(sqlUpdate);
                ps.setLong(1, range);
                ps.setString(2, sequenceName);
                int ret = ps.executeUpdate();
                conn.commit();
                if(ret == 1) {
                    SequenceCache cache = new PaasSequenceCache(currVal+1, currVal+range,range);
                    if(log.isDebugEnabled()) {
                        log.debug("获取序列缓存对象:" + JSONObject.fromObject(cache));
                    }
                    return cache;
                }               
            }
        } catch (SQLException e) {
            log.error("序列["+sequenceName+"]读取错误",e);
        } finally {
            try {
                if(rs != null)
                    rs.close();
            } catch (Exception e) {
                log.error("序列["+sequenceName+"]读取错误",e);
            }
            try {
                if(ps != null)
                    ps.close();
            } catch (Exception e) {
                log.error("序列["+sequenceName+"]读取错误",e);
            }
            try {
                if(conn != null)
                    conn.close();
            } catch (Exception e) {
                log.error("序列["+sequenceName+"]读取错误",e);
            }
        }
        return null;
    }
    
    public int updateSequence(String sequenceName){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);
            if(sqlUpdate  == null || sqlUpdate.length() == 0) {
                sqlUpdate = "update " + sequenceTable + " set value= value+? where name = ?";
            }
            ps = conn.prepareStatement(sqlUpdate);
            ps.setLong(1, range);
            ps.setString(2, sequenceName);
            int ret = ps.executeUpdate();
            conn.commit();
            return ret;
        } catch (SQLException e) {
            log.error("序列["+sequenceName+"]更新失败",e);
        } finally {
            try {
                if(ps != null)
                    ps.close();
            } catch (Exception e) {
                log.error("序列["+sequenceName+"]更新失败",e);
            }
            try {
                if(conn != null)
                    conn.close();
            } catch (Exception e) {
                log.error("序列["+sequenceName+"]更新失败",e);
            }
        }
        return -1;
    }
    public void init() {
        try {
            process(cc.getConfAndWatch(confPath, this));
            
        } catch (PaasException e) {
            log.error("序列service初始化失败。",e);
        }
    }
    
    public void process(String conf) {
        if(log.isInfoEnabled()) {
            log.info("new log configuration is received: " + conf);
        }
        if(conf != null && !conf.equals(dbConf)) {
            dbConf = conf;
            this.conf = JSONObject.fromObject(dbConf);
            initDb();
            String tableName = this.conf.getString("sequenceTable");
            if(tableName != null && !tableName.equals(sequenceTable)) {
                sequenceTable = tableName;
                sqlSelect = null;
                sqlUpdate = null;
            }
        }
    }
    
    public void initDb() {              
        if(conf != null) {
            if(log.isDebugEnabled()) {
                log.debug("init Sequence DataSource");
                log.debug("----dbRule:" + dbConf);
            }
            BasicDataSource ds = new BasicDataSource();
            if(conf.containsKey("driver")) {
                ds.setDriverClassName(conf.getString("driver"));
            }
            String username = "";
            if(conf.containsKey("username")) {
                username = conf.getString("username");
                ds.setUsername(username);
            }
            if(conf.containsKey("password")) {
                ds.setPassword(CipherUtil.decrypt(conf.getString("password")));
            }
            String monUrl = "";
            if(conf.containsKey("url")) {
                monUrl = conf.getString("url");
                ds.setUrl(monUrl);
            }
            //统一序列；
//            ds.setJmxName(Constants.JMX_OBJECT_NAME+"SEQUENCE");
            setDbMonJmxInfo(ds,"SEQUENCE",monUrl);
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
            db = ds;
        }
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
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

    public String getSequenceTable() {
        return sequenceTable;
    }

    public void setSequenceTable(String sequenceTable) {
        this.sequenceTable = sequenceTable;
    }

    private void setDbMonJmxInfo(BasicDataSource ds, String username,
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

}

