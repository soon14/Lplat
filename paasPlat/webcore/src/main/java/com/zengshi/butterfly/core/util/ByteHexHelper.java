package com.zengshi.butterfly.core.util;

public class ByteHexHelper {

	/**
	 * Convert byte[] to hex
	 * string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
	 * 
	 * @param src byte[] data
	 * 
	 * @return hex string
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * 16进制的字符串表示转成字节数组
	 * 
	 * @param hexString
	 *            16进制格式的字符串
	 * @return 转换后的字节数组
	 **/
	public static byte[] hexStr2ByteArray(String hexString) {
		if (hexString == null || "".equals(hexString))
			throw new IllegalArgumentException(
					"this hexString must not be empty");

		hexString = hexString.toLowerCase();
		final byte[] byteArray = new byte[hexString.length() / 2];
		int k = 0;
		for (int i = 0; i < byteArray.length; i++) {
			// 因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
			// 将hex 转换成byte "&" 操作为了防止负数的自动扩展
			// hex转换成byte 其实只占用了4位，然后把高位进行右移四位
			// 然后“|”操作 低四位 就能得到 两个 16进制数转换成一个byte.
			//
			byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
			byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
			byteArray[i] = (byte) (high << 4 | low);
			k += 2;
		}
		return byteArray;
	}

	/**
	 * 16进制字符串转换成byte数组
	 * 
	 * @param 16进制字符串
	 * @return 转换后的byte数组
	 */
	public static byte[] hex2Byte(String hex) {
		String digital = "0123456789ABCDEF";
		char[] hex2char = hex.toCharArray();
		byte[] bytes = new byte[hex.length() / 2];
		int temp;
		for (int i = 0; i < bytes.length; i++) {
			// 其实和上面的函数是一样的 multiple 16 就是右移4位 这样就成了高4位了
			// 然后和低四位相加， 相当于 位操作"|"
			// 相加后的数字 进行 位 "&" 操作 防止负数的自动扩展. {0xff byte最大表示数}
			temp = digital.indexOf(hex2char[2 * i]) * 16;
			temp += digital.indexOf(hex2char[2 * i + 1]);
			bytes[i] = (byte) (temp & 0xff);
		}
		return bytes;
	}
}
