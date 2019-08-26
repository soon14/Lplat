/**
 * 
 */
package com.zengshi.butterfly.core.util;

/**
 * 64进制用的工具类
 * 
 */
public class RadixHelper {
	final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
			'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
			'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
			'Z','%','-' };

	public static String toString(long i) {
		StringBuilder tmp = new StringBuilder();
		while(true) {
			long x=i & 0x3f;
			i = i >> 6;
			
			tmp.insert(0,digits[(int)x]);
			if(i <= 0) break;
		}
		return tmp.toString();
	}
	
	
	public static long toInteger(String value) {
		return toInteger(value,64);
	}
	/**
	 * 只支持64进制的转换
	 * @param value
	 * @return
	 */
	public static long toInteger(String value, int radix) {
		long v=0;
		for (int i = 0; i < value.getBytes().length; i++) {
			char c=value.charAt(i);
			int idx=0;
			int j=0;
			for(j=0;j<digits.length;j++) {
				if(digits[j] == c) {
					idx=j;
					break;
				}
			}
			
			v= v << 6 | idx;
		}
		
		return v;
	}
	
	public   static   void   main(String[]   arg)
    {
		String b=toString(999999998);
		System.out.println(b);
		System.out.println(toInteger(b));
    } 
}
