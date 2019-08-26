/**
 * 
 */
package com.zengshi.butterfly.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 */
public class StreamHelper {

	/**
	 * 将输入流转为字符串
	 * @param in
	 * @param charset
	 * @return
	 */
	public static String inputstreamToString(InputStream in,String charset) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuffer buffer = new StringBuffer();
        String str = null;
        try {
        	
            while ((str = reader.readLine()) != null) {
            	if(charset != null) {
            		buffer.append(new String(str.getBytes(),charset) + System.getProperty("line.separator"));
            	}else {
            		buffer.append(new String(str.getBytes()) +  System.getProperty("line.separator"));
            	}
                
            }
            return buffer.toString();
        } catch (IOException e) {
            return buffer.toString();
        }
    }
	
	/**
	 * 使用系统默认的编码将输入流转为字符串
	 * @param in
	 * @return
	 */
	public static String inputstreamToString(InputStream in) {
		return inputstreamToString(in, null);
    }
	
}
