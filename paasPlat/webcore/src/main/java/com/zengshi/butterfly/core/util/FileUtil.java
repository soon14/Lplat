/**
 * 
 */
package com.zengshi.butterfly.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * 
 */
public class FileUtil {

	public static final int DEFAULT_BUFFER_SIZE =2048;
	
	public static String file2String(String path, String encoding) {
		InputStreamReader reader = null;
		StringWriter writer = new StringWriter();
		InputStream is = null;
		try {
			is=Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
			if(is == null) {
				is=new FileInputStream(path);
			}
			if (encoding == null || "".equals(encoding.trim())) {
				reader = new InputStreamReader(is);
			} else {
				reader = new InputStreamReader(is);
			}
			// 将输入流写入输出流
			char[] buffer = new char[DEFAULT_BUFFER_SIZE];
			int n = 0;
			while (-1 != (n = reader.read(buffer))) {
				writer.write(buffer, 0, n);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if(is != null) try {
				is.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		// 返回转换结果
		if (writer != null)
			return writer.toString();
		else
			return null;
	}
	
	public static String readFile(String path) {
		return file2String(path,null);
	}
	
	/**
	 * 文件是否存在
	 * @param fileName
	 * @return
	 */
	public static boolean fileExists(String fileName){
		File file = new File(fileName);
		if(file.exists()){
			return true;
		}	
		return false;
	}
	
	/**
	 * 删除文件
	 * @param path
	 * @return
	 */
	public static boolean removeFile(String path){
		File file = new File(path);
		if(file.exists() && file.isFile()){
			file.delete();
		}
		return true;
	}
	
	/**
	 * 取文件名
	 * @param f
	 * @return
	 */
	public static String getFileName(String f) {
		return getFileName(f,"");
	}
	
	/**
	 * 取文件名
	 * @param f
	 * @return
	 */
	public static String getFileName(String f,String prefix) {
		String fname = "";
		int i = f.lastIndexOf('.');
		if (i > 0 && i < f.length() - 1) {
			fname = f.substring(0, i);
		}
		return prefix+fname;
	}
	
	/**
	 * 取文件扩展名
	 * @param f
	 * @return
	 */
	public static String getExtension(String f) {
		String ext = "";
		int i = f.lastIndexOf('.');
		if (i > 0 && i < f.length() - 1) {
			ext = f.substring(i + 1);
		}
		return ext;
	}
	
	public static void main(String [] arg) {
		
		String file=FileUtil.readFile("/home/yangqx/work/share/workspace/UltraNote/ailk-core/src/test/resources/yn0001-request.xml");
		System.out.println(file);
	}
}
