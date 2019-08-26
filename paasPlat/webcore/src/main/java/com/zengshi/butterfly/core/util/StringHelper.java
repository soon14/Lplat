/**
 * 
 */
package com.zengshi.butterfly.core.util;

/**
 *
 */
public class StringHelper {

	/**
	 * 返回一个字符串，如果origValue为Null，则返回defaultValue指定的默认值
	 * @param origValue
	 * @param defaultValue
	 * @return
	 */
	public static  String StringValue(String origValue,String defaultValue) {
		if(origValue == null) {
			return defaultValue;
		}
		return origValue;
	}
	/**
	 * 如果origvalue为空，则返回空
	 * @param origValue
	 * @return
	 */
	public static String StringValue(String origValue) {
		return StringValue(origValue,"");
	}
}
