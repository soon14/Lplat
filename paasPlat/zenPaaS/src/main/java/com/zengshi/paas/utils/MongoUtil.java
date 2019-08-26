package com.zengshi.paas.utils;

import com.zengshi.paas.mongo.MongoSVC;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 */
public class MongoUtil {

    private static MongoSVC mongoSVC;
    static{
        mongoSVC=PaasContextHolder.getContext().getBean("mongoSVC",MongoSVC.class);
    }

    public static String insert(String collectionName, String doc){
        return mongoSVC.insert(collectionName,doc);
    }

    public static String insert(String collectionName, JSONObject doc){
        return mongoSVC.insert(collectionName,doc);
    }

    public static String insert(String collectionName, Map docMap){
        return mongoSVC.insert(collectionName,docMap);
    }

    public static String insert(String collectionName, Object obj){
        return mongoSVC.insert(collectionName,obj);
    }

    public static List<String> insertList(String collectionName, JSONObject... docs){
        return mongoSVC.insertList(collectionName,docs);
    }

    public static List<String> insertList(String collectionName,List<Map> docMapList){
        return mongoSVC.insertList(collectionName,docMapList);
    }

    public static DBCollection getDBCollection(String collectionName){
        return mongoSVC.getDBCollection(collectionName);
    }

    public static DBObject findOne(String collectionName, DBObject condition, DBObject field, DBObject orderBy){

        return mongoSVC.findOne(collectionName,condition,field,orderBy);
    }

    public static WriteResult update(String collectionName, DBObject query, DBObject o){

        return mongoSVC.update(collectionName,query,o);
    }

    public static WriteResult remove(String collectionName,DBObject query){

        return mongoSVC.remove(collectionName,query);
    }
}
