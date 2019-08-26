package com.zengshi.paas.mongo;

import com.zengshi.paas.utils.CipherUtil;
import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.mongodb.util.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * MongoDB访问封装类
 *
 */
public class MongoDBClient {
	private static final Logger log = Logger.getLogger(MongoDBClient.class);
	
	private static final String IP_KEY = "ip";
	private static final String PORT_KEY = "port";
	private static final String FILE_NAME = "filename";
	
	
	private MongoClient mongo = null;
	
	public MongoDBClient(String addr) {
		try {
			JSONArray array = JSONArray.fromObject(addr);
			if(array != null && array.size() > 0) {
				int size = array.size();
				JSONObject json = null;
				ArrayList<ServerAddress> sa = new ArrayList<ServerAddress>();
				for(int i=0; i<size; i++) {
					json = (JSONObject)array.get(i);
					sa.add(new ServerAddress(json.getString(IP_KEY), json.getInt(PORT_KEY)));
				}
				mongo = new MongoClient(sa);
				
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
	
	public MongoDBClient(String addr, String database, String userName, String password) {
		try {
			JSONArray array = JSONArray.fromObject(addr);
			if(array != null && array.size() > 0) {
				int size = array.size();
				JSONObject json = null;
				ArrayList<ServerAddress> sa = new ArrayList<ServerAddress>();
				for(int i=0; i<size; i++) {
					json = (JSONObject)array.get(i);
					sa.add(new ServerAddress(json.getString(IP_KEY), json.getInt(PORT_KEY)));
				}
				if(userName != null && userName.length() > 0 && password != null && password.length() > 0) {
					String orignPwd = CipherUtil.decrypt(password);
					MongoCredential credential = MongoCredential.createMongoCRCredential(userName, database, orignPwd.toCharArray());
					mongo = new MongoClient(sa, Arrays.asList(credential));
//					String uri="mongodb://"+userName+":"+orignPwd+"@"+json.getString(IP_KEY)+":"+json.getInt(PORT_KEY)+"/"+database;
//
//					mongo=new MongoClient(new MongoClientURI(uri));
//					log.info("============================ "+uri);
				}else {
					mongo = new MongoClient(sa);
				}
				
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
	
	public String insert(String dbName, String collectionName, String doc) {
		DBObject dbObj = (DBObject)JSON.parse(doc);
		ObjectId _id=ObjectId.get();
		dbObj.put("_id",_id);
		mongo.getDB(dbName).getCollection(collectionName).insert(dbObj);
		return _id.toString();
	}
	
	public String insert(String dbName, String collectionName, JSONObject doc) {
		DBObject dbObj = (DBObject)JSON.parse(doc.toString() );
		ObjectId _id=ObjectId.get();
		dbObj.put("_id",_id);
		mongo.getDB(dbName).getCollection(collectionName).insert(dbObj);
		return _id.toString();
	}

	@SuppressWarnings("rawtypes")
	public String insert(String dbName, String collectionName, Map docMap) {
		DBObject dbObj = new BasicDBObject(docMap);
		ObjectId _id=ObjectId.get();
		dbObj.put("_id",_id);
		mongo.getDB(dbName).getCollection(collectionName).insert(dbObj);
		return _id.toString();
	}

	public String insert(String dbName, String collectionName, Object obj) {
		if(null==obj){
			return null;
		}
		JSONObject jsonObject=JSONObject.fromObject(obj);

		return insert(dbName,collectionName,jsonObject);
	}

	public List<String> insertList(String dbName,String collectionName,JSONObject... docs){
		if(null==docs || docs.length==0){
			return null;
		}
		List<String> ids=new ArrayList<String>();
		List<DBObject> dbDocs=new ArrayList<DBObject>();
		for(JSONObject doc : docs){
			DBObject dbObj = (DBObject)JSON.parse(doc.toString() );
			dbDocs.add(dbObj);
			ObjectId _id=ObjectId.get();
			dbObj.put("_id",_id);
			ids.add(_id.toString());
		}
		mongo.getDB(dbName).getCollection(collectionName).insert(dbDocs);
		return ids;
	}
	public List<String> insertList(String dbName,String collectionName,List<Map> docMapList){
		if(CollectionUtils.isEmpty(docMapList)){
			return null;
		}
		List<String> ids=new ArrayList<String>();
		List<DBObject> dbDocs=new ArrayList<DBObject>();
		for(Map docMap : docMapList){
			DBObject dbObj = new BasicDBObject(docMap);
			ObjectId _id=ObjectId.get();
			dbObj.put("_id",_id);
			ids.add(_id.toString());
			dbDocs.add(dbObj);
		}
		mongo.getDB(dbName).getCollection(collectionName).insert(dbDocs);
		return ids;
	}


	public DBCollection getDBCollection(String dbName,String collectionName){
		return mongo.getDB(dbName).getCollection(collectionName);
	}

	public DBObject findOne(String dbName,String collectionName,DBObject condition,DBObject field,DBObject orderBy){
		DBCollection db=getDBCollection(dbName,collectionName);
		return db.findOne(condition,field,orderBy);
	}

	public WriteResult update(String dbName,String collectionName,DBObject query, DBObject o){
		DBCollection db=getDBCollection(dbName,collectionName);
		WriteResult wr= db.update(query, o);
		return wr;
	}

	public WriteResult remove(String dbName,String collectionName,DBObject query){
		DBCollection db=getDBCollection(dbName,collectionName);
		return db.remove(query);
	}
	
	
	public String saveFile(String dbName, byte[] byteFile, String fileName, String fileType) {
		GridFS fs = new GridFS(mongo.getDB(dbName));
		GridFSInputFile dbFile = null;
		try {
			dbFile = fs.createFile(byteFile);
		} catch (Exception e) {
			log.error("保存文件失败。",e);
		}
		dbFile.setContentType(fileType);
		dbFile.setFilename(fileName);
		dbFile.put("fileName", fileName);
		dbFile.save();
		return dbFile.getId().toString();
	}

	public String saveFile(String dbName, InputStream ips, String fileName, String fileType) {
		GridFS fs = new GridFS(mongo.getDB(dbName));
		GridFSInputFile dbFile = null;
		try {
			dbFile = fs.createFile(ips,fileName,true);
		} catch (Exception e) {
			log.error("保存文件失败。",e);
		}
		dbFile.setContentType(fileType);
		dbFile.setFilename(fileName);
		dbFile.put("fileName", fileName);
		dbFile.save();
		return dbFile.getId().toString();
	}
	
	public String saveFile(String dbName, String fileName, String fileType) {
		if(fileName == null) {
			return null;
		}
		String name = fileName.substring(fileName.lastIndexOf("/")+1);
		GridFS fs = new GridFS(mongo.getDB(dbName));
		File file = new File(fileName);
		GridFSInputFile dbFile = null;
		try {
			dbFile = fs.createFile(file);
		} catch (IOException e) {
			log.error("保存文件失败。",e);
		}
		dbFile.setContentType(fileType);
		dbFile.setFilename(name);
		dbFile.save();
		return dbFile.getId().toString();
	}
	
	public void deleteFile(String dbName, String fileId) {
		if(fileId == null) {
			return ;
		}
		GridFS fs = new GridFS(mongo.getDB(dbName));
		GridFSDBFile dbFile = fs.findOne(new ObjectId(fileId));
		if(dbFile == null) {
			return ;
		}
		fs.remove(dbFile);
	}
	
	public void deleteFileByName(String dbName, String fileName) {
		if(fileName == null) {
			return ;
		}
		GridFS fs = new GridFS(mongo.getDB(dbName));
		GridFSDBFile dbFile = fs.findOne(new BasicDBObject(FILE_NAME, fileName));
		if(dbFile == null) {
			return ;
		}
		fs.remove(dbFile);
	}
	
	
	
	public byte[] readFile(String dbName, String fileId) {
		if(StringUtils.isBlank(fileId)) {
			return null;
		}
		GridFS fs = new GridFS(mongo.getDB(dbName));
		GridFSDBFile dbFile = fs.findOne(new ObjectId(fileId));
		if(dbFile == null) {
			return null;
		}
		InputStream is = null;
		try {		
			int len = (int)dbFile.getLength();
			byte[] ret = new byte[len];
			is = dbFile.getInputStream();
			int tmp = 0;
			int i=0;
			while((tmp = is.read()) != -1 && i < len) {
				ret[i] = (byte)tmp;
				i++;
			}
			return ret;
		} catch (IOException e) {
			log.error(e.getMessage(),e);
			return null;
		}finally {
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error(e.getMessage(),e);
				}
			}
		}		
	}

	public InputStream readFile2ips(String dbName,String fileId){
		if(StringUtils.isBlank(fileId)) {
			return null;
		}
		GridFS fs = new GridFS(mongo.getDB(dbName));
		GridFSDBFile dbFile = fs.findOne(new ObjectId(fileId));
		if(dbFile == null) {
			return null;
		}

		return dbFile.getInputStream();
	}
	
	public byte[] readFileByName(String dbName, String fileName) {
		if(fileName == null) {
			return null;
		}
		GridFS fs = new GridFS(mongo.getDB(dbName));
		GridFSDBFile dbFile = fs.findOne(new BasicDBObject(FILE_NAME, fileName));
		if(dbFile == null) {
			return null;
		}
		InputStream is = null;
		try {		
			int len = (int)dbFile.getLength();
			byte[] ret = new byte[len];
			is = dbFile.getInputStream();
			int tmp = 0;
			int i=0;
			while((tmp = is.read()) != -1 && i < len) {
				ret[i] = (byte)tmp;
				i++;
			}
			return ret;
		} catch (IOException e) {
			log.error(e.getMessage(),e);
			return null;
		}finally {
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error(e.getMessage(),e);
				}
			}
		}
	}

	public void readFile(String dbName, String fileId, OutputStream ops){
		if(fileId == null) {
			return ;
		}
		GridFS fs = new GridFS(mongo.getDB(dbName));
		GridFSDBFile dbFile = fs.findOne(new ObjectId(fileId));
		if(dbFile == null) {
			return ;
		}
		try {
			dbFile.writeTo(ops);
		} catch (IOException e) {
			log.error("读取mongodb存储的文件流错误。",e);
		}
	}
	
	public void readFile(String dbName, String fileId, String localFileName) {
		if(fileId == null) {
			return ;
		}
		GridFS fs = new GridFS(mongo.getDB(dbName));
		GridFSDBFile dbFile = fs.findOne(new ObjectId(fileId));
		if(dbFile == null) {
			return ;
		}
		File file = new File(localFileName);
		try {
			dbFile.writeTo(file);
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
		
	}
	
	public void readFileByName(String dbName, String fileName, String localFileName) {
		if(fileName == null) {
			return ;
		}
		GridFS fs = new GridFS(mongo.getDB(dbName));
		GridFSDBFile dbFile = fs.findOne(new BasicDBObject(FILE_NAME, fileName));
		if(dbFile == null) {
			return ;
		}
		File file = new File(localFileName);
		try {
			dbFile.writeTo(file);
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
		
	}
	
    public String updateFile(String dbName, byte[] byteFile, String fileId, String fileName,
            String fileType) throws Exception {
        if (byteFile == null)
            return null;
        GridFS fs = new GridFS(mongo.getDB(dbName));
        deleteFile(dbName, fileId);
        GridFSInputFile dbFile = fs.createFile(byteFile);
        dbFile.setId(new ObjectId(fileId));
        dbFile.setContentType(fileType);
        dbFile.setFilename(fileName);
        dbFile.put("fileName", fileName);
        dbFile.save();
        return dbFile.getId().toString();
    }
    
    public String updatePropertyOfFile(String dbName, String fileId, String fileName,
            String fileType) throws Exception {
        
        if(null==fileId){
            return null;
        }
        
        GridFS fs = new GridFS(mongo.getDB(dbName));
        GridFSDBFile dbFile = fs.findOne(new ObjectId(fileId));
        if(dbFile == null) {
            return null;
        }
        InputStream is=dbFile.getInputStream();
        String ofileName=dbFile.getFilename();
        String ofileType=dbFile.getContentType();
        
        GridFSInputFile ndbFile=fs.createFile(is);
        fileName=fileName==null?ofileName:fileName;
        fileType=fileType==null?ofileType:fileType;
        
        ndbFile.setFilename(fileName);
        ndbFile.setContentType(fileType);
        ndbFile.save();
        fs.remove(dbFile);
        return ndbFile.getId().toString();
    }
    
    public Date readFileAndUpdateTime(String dbName, String fileId, String localFileName) {
        if (fileId == null) {
            return null;
        }
        GridFS fs = new GridFS(mongo.getDB(dbName));
        GridFSDBFile dbFile = fs.findOne(new ObjectId(fileId));
        if (dbFile == null) {
            return null;
        }
        File file = new File(localFileName);
        try {
            dbFile.writeTo(file);
            return dbFile.getUploadDate();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date readUpdateTime(String dbName, String fileId) {
        if (fileId == null) {
            return null;
        }
        GridFS fs = new GridFS(mongo.getDB(dbName));
        GridFSDBFile dbFile = fs.findOne(new ObjectId(fileId));
        if (dbFile == null) {
            return null;
        }
        try {
            return dbFile.getUploadDate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getFileName(String dbName, String fileId) {
        if (fileId == null) {
            return null;
        }
        GridFS fs = new GridFS(mongo.getDB(dbName));
        GridFSDBFile dbFile = fs.findOne(new ObjectId(fileId));
        if (dbFile == null) {
            return null;
        }
        return dbFile.getFilename();
    }

	public String getContentType(String dbName,String fileId){
		if (fileId == null) {
			return null;
		}
		GridFS fs = new GridFS(mongo.getDB(dbName));
		GridFSDBFile dbFile = fs.findOne(new ObjectId(fileId));
		if (dbFile == null) {
			return null;
		}
		return dbFile.getContentType();
	}

	public long getFileSize(String dbName,String fileId){
		if (fileId == null) {
			return 0;
		}
		GridFS fs = new GridFS(mongo.getDB(dbName));
		GridFSDBFile dbFile = fs.findOne(new ObjectId(fileId));
		if (dbFile == null) {
			return 0;
		}
		return dbFile.getLength();
	}

    /**
     * 带条件查询
     *
     * @param dbName
     * @param condition
     */
    public List<GridFSDBFile> queryWithCondition(String dbName, Map<String, String> condition) {
        GridFS fs = new GridFS(mongo.getDB(dbName));
        if (condition != null) {
            DBObject ob = new BasicDBObject();
            for (String key : condition.keySet()) {
                ob.put(key, condition.get(key));
            }
            return fs.find(ob);
        }
        return null;
    }

}
