package com.zengshi.butterfly.core.afs;

import java.io.InputStream;
import java.io.OutputStream;


public interface AFile {

	 /**
	  * 创建文件
	  */
	 public void newFile() throws Exception;
	 
	 /**
	  * 当前文件下的所有子文件
	  * @return
	  */
	 public String[] list() throws Exception;
	 
	 /**
	  * 创建目录
	  * @return
	  */
	 public boolean mkdir() throws Exception;
	 
	 public boolean mkdirs() throws Exception;
	 
	 /**
	  * 重命名
	  * @param dest
	  * @return
	  */
	 public boolean renameTo(AFile dest) throws Exception;
	 
	 /**
	  * 读
	  * @return
	  */
	 public InputStream read(String fileName) throws Exception;
	 
	 /**
	  * 写
	  * @param os
	  */
	 public void write(OutputStream os) throws Exception;
	// public long getTotalSpace();
	 
	 
	 public long save(String fileName,InputStream is) throws Exception;

}
