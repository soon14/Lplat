package com.zengshi.paas.mongo;

import com.zengshi.paas.ConfigurationCenter;
import com.zengshi.paas.ConfigurationWatcher;
import com.zengshi.paas.PaasException;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/** {server:[{ip:'10.8.0.213',port:'27017'}],dbname:'imdb',username:'ecp_im',password:'c2a9059d1f41007e'}
 */
public class MongoSVCImpl implements ConfigurationWatcher,MongoSVC {
    private static final Logger log = Logger.getLogger(MongoSVCImpl.class);

    private String confPath = "/com/zengshi/paas/mongodb/conf";
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String SERVER_KEY="server";
    private static final String DBNAME_KEY="dbname";

    private String username=null;
    private String password=null;
    private String server=null;
    private String dbname=null;
    private MongoDBClient mongo = null;
    private ConfigurationCenter cc = null;

    public void init() {
        try {
            process(cc.getConfAndWatch(confPath, this));

        } catch (PaasException e) {
            log.error("mongodb连接初始化错误。",e);
        }
    }

    @Override
    public void process(String conf) {
        if(log.isInfoEnabled()) {
            log.info("new log configuration is received: " + conf);
        }
        JSONObject json = JSONObject.fromObject(conf);
        boolean changed = false;

        if(json.containsKey(SERVER_KEY) && !json.getString(SERVER_KEY).equals(server)) {
            changed = true;
            server = json.getString(SERVER_KEY);
        }
        if(json.containsKey(DBNAME_KEY) && !json.getString(DBNAME_KEY).equals(dbname)) {
            changed = true;
            dbname = json.getString(DBNAME_KEY);
        }
        if(json.containsKey(USERNAME_KEY) && !json.getString(USERNAME_KEY).equals(username)) {
            changed = true;
            username = json.getString(USERNAME_KEY);
        }
        if(json.containsKey(PASSWORD_KEY) && !json.getString(PASSWORD_KEY).equals(password)) {
            changed = true;
            password = json.getString(PASSWORD_KEY);
        }

        if(changed) {
            if(server != null) {
                mongo = new MongoDBClient(server,dbname, username, password);
                if(log.isInfoEnabled()) {
                    log.info("log server address is changed to " + server);
                }
            }
        }
    }

    @Override
    public String insert(String collectionName, String doc) {
        return mongo.insert(dbname,collectionName,doc);
    }

    @Override
    public String insert(String collectionName, JSONObject doc) {
        return mongo.insert(dbname,collectionName,doc);
    }

    @Override
    public String insert(String collectionName, Map docMap) {
        return mongo.insert(dbname,collectionName,docMap);
    }

    @Override
    public String insert(String collectionName, Object obj) {
        return mongo.insert(dbname,collectionName,obj);
    }

    @Override
    public List<String> insertList(String collectionName, JSONObject... docs) {
        return mongo.insertList(dbname,collectionName,docs);
    }

    @Override
    public List<String> insertList(String collectionName, List<Map> docMapList) {
        return mongo.insertList(dbname,collectionName,docMapList);
    }

    @Override
    public DBCollection getDBCollection(String collectionName) {
        return mongo.getDBCollection(dbname,collectionName);
    }

    @Override
    public DBObject findOne(String collectionName, DBObject condition, DBObject field, DBObject orderBy) {
        return mongo.findOne(dbname,collectionName,condition,field,orderBy);
    }

    @Override
    public WriteResult update( String collectionName, DBObject query, DBObject o) {
        return mongo.update(dbname,collectionName,query,o);
    }

    @Override
    public WriteResult remove(String collectionName, DBObject query) {
        return mongo.remove(dbname,collectionName,query);
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
}
