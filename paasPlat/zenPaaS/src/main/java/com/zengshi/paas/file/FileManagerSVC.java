package com.zengshi.paas.file;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mongodb.gridfs.GridFSDBFile;

/**
 * 文件服务接口类
 *
 */
public interface FileManagerSVC {
	public String saveFile(String fileName, String fileType);
	public String saveFile(byte[] byteFile, String fileName, String fileType);
	public String saveFile(InputStream inputStream, String fileName, String fileType);
	public byte[] readFile(String fileId);
	public void readFile(String fileId, String localFileName);
	public void deleteFile(String fileId);
	public byte[] readFileByName(String fileName);
	public void readFileByName(String fileName, String localFileName);
	public void deleteFileByName(String fileName);
	
	Date readFileAndUpdateTime(String fileId, String localFileName);
	Date readUpdateTime(String fileId);
	List<GridFSDBFile> queryWithCondition(Map<String, String> condition);
	String updateFile(byte[] byteFile, String fileId, String fileName,String fileType) throws Exception;
	
	String updatePropertyOfFile(String fileId, String fileName,String fileType) throws Exception;
	
	String getFileName(String fileId);

	String  getFileType(String fileId);

	long getFileSize(String fileId);

	void readFile(String fileId, OutputStream ops);

	InputStream readFile2ips(String fileId);
}
