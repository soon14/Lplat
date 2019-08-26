package com.zengshi.paas.mongo;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 */
public interface MongoSVC {
    String insert(String collectionName, String doc);

    String insert(String collectionName, JSONObject doc);

    String insert(String collectionName, Map docMap);

    String insert(String collectionName, Object obj);

    List<String> insertList(String collectionName, JSONObject... docs);

    List<String> insertList(String collectionName,List<Map> docMapList);

    DBCollection getDBCollection(String collectionName);

    DBObject findOne(String collectionName, DBObject condition, DBObject field, DBObject orderBy);

    WriteResult update(String collectionName, DBObject query, DBObject o);

    WriteResult remove(String collectionName,DBObject query);

}
