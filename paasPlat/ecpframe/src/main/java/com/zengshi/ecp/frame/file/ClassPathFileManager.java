/**
 * 
 */
package com.zengshi.ecp.frame.file;

import com.zengshi.paas.file.FileManagerSVC;
import com.mongodb.gridfs.GridFSDBFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 本地classpath下文件的處理；
 *
 */
public class ClassPathFileManager implements FileManagerSVC {
	
	private static final Logger logger = LoggerFactory.getLogger(ClassPathFileManager.class);

	/* (non-Javadoc)
	 * @see com.zengshi.paas.client.file.FileManager#deleteFile(java.lang.String)
	 */
	public void deleteFile(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.zengshi.paas.client.file.FileManager#deleteFileByName(java.lang.String)
	 */
	public void deleteFileByName(String arg0) {
		// TODO Auto-generated method stub

	}

	/* 
	 * 根据文件名，读取文件；
	 * (non-Javadoc)
	 * @see com.zengshi.paas.client.file.FileManager#readFile(java.lang.String)
	 * 
	 */
	public byte[] readFile(String arg0) {
		return this.readFileByName(arg0);
	}

	/* (non-Javadoc)
	 * @see com.zengshi.paas.client.file.FileManager#readFile(java.lang.String, java.lang.String)
	 */
	public void readFile(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	/* 
	 * 根据文件名，读取文件；
	 * (non-Javadoc)
	 * @see com.zengshi.paas.client.file.FileManager#readFileByName(java.lang.String)
	 */
	public byte[] readFileByName(String arg0) {
	    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
	    
		//Resource fileResource = new ClassPathResource(arg0);
		InputStream inStream = null;
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();  
		try {
		    //File fileResource = ResourceUtils.getFile(arg0);
		    Resource[] fileResources = resolver.getResources(arg0);
		    if(fileResources == null || fileResources.length == 0){
		        throw new IOException("no file match;"+arg0);
		    }
			inStream = fileResources[0].getInputStream();
			int length = 1024;
	        byte[] buff = new byte[length];  
	        int rc = 0;  
	        while ((rc = inStream.read(buff, 0, length)) > 0) {  
	            swapStream.write(buff, 0, rc);  
	        }
	        
	        byte[] bts = swapStream.toByteArray();
	        if(logger.isDebugEnabled()){
	        	logger.debug(new String(bts));
	        }
	        return bts;
	        
		} catch (IOException e) {
			
			e.printStackTrace();
			return null;
		} finally {
			if(swapStream != null){
				try{
					swapStream.close();
				} catch(IOException e){
					e.printStackTrace();
				}
			}
			
			if(inStream != null){
				try{
					inStream.close();
				} catch(IOException e){
					e.printStackTrace();
				}
				
			}
		}
		
        
	}

	/* (non-Javadoc)
	 * @see com.zengshi.paas.client.file.FileManager#readFileByName(java.lang.String, java.lang.String)
	 */
	public void readFileByName(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.zengshi.paas.client.file.FileManager#saveFile(java.lang.String, java.lang.String)
	 */
	public String saveFile(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.zengshi.paas.client.file.FileManager#saveFile(byte[], java.lang.String, java.lang.String)
	 */
	public String saveFile(byte[] arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String saveFile(InputStream inputStream, String fileName, String fileType) {
		return null;
	}

	@Override
    public Date readFileAndUpdateTime(String fileId, String localFileName) {
        return null;
    }

    @Override
    public Date readUpdateTime(String fileId) {
        return null;
    }

    @Override
    public List<GridFSDBFile> queryWithCondition(Map<String, String> condition) {
        return null;
    }

    @Override
    public String updateFile(byte[] byteFile, String fileId, String fileName, String fileType)
            throws Exception {
        return null;
    }

    @Override
    public String updatePropertyOfFile(String fileId, String fileName, String fileType)
            throws Exception {
        return null;
    }

    @Override
    public String getFileName(String fileId) {
        return null;
    }

	@Override
	public String getFileType(String fileId) {
		return null;
	}

	@Override
	public void readFile(String fileId, OutputStream ops) {

	}

	@Override
	public InputStream readFile2ips(String fileId) {
		return null;
	}

	@Override
	public long getFileSize(String fileId) {
		// TODO Auto-generated method stub
		return 0;
	}
}
