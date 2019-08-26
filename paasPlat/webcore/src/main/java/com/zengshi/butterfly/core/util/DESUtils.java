package com.zengshi.butterfly.core.util;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class DESUtils {
	
	public static final Log log = LogFactory.getLog(DESUtils.class);
	
	/**
	 * 报表json密钥
	 */
	private final static String DES = "DES";
	
	/**
	 * 加密
	 * @param src 数据源
	 * @param key 密钥，长度必须是8的倍数
	 * @return 返回加密后的数据
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		System.out.println("数据长度："+(src.length%8)+",密钥长度："+key.length);
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		/**加密方式/模式/填充方式，PKCS7Padding填充方式 jdk不支持 , BC 是 provider **/
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS7Padding","BC");
//		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
//		Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");
		
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
		return cipher.doFinal(src);
	}

	/**
	 * 解密
	 * 
	 * @param src 数据源
	 * @param key 密钥，长度必须是8的倍数
	 * @return 返回解密后的原始数据
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		System.out.println("解密数据长度："+(src.length%8)+",密钥长度："+key.length);
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS7Padding","BC");
//		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
//		Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
		return cipher.doFinal(src);
	}

	/**
	 * 密码解密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public final static String decrypt(String hex,String pwd) {
		try {
			System.out.println("解密前："+hex);
			String data = new String(decrypt(hex2byte(hex.getBytes("UTF-8")), pwd.getBytes()), "UTF-8");
//			String data = new String(decrypt(decryptBASE64(hex), pwd.getBytes()), "UTF-8");
			System.out.println("解密后："+data);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 密码加密
	 * 
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public final static String encrypt(String data,String pwd) {
		try {
			String hex =  byte2hex(encrypt(data.getBytes("UTF-8"), pwd.getBytes()));
//			String hex =  encryptBASE64(encrypt(data.getBytes("UTF-8"), pwd.getBytes()));
			
			return hex;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 二行制转字符串
	 * 
	 * @param b
	 * 
	 * @return
	 */

	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;

		}
		return hs.toUpperCase();
	}

	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0) throw new IllegalArgumentException("长度不是偶数");
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}
	
	/**
	 * BASE64解密
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptBASE64(String key) throws Exception {
		return (new BASE64Decoder()).decodeBuffer(key);
	}

	/**
	 * BASE64加密
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encryptBASE64(byte[] key) throws Exception {
		return (new BASE64Encoder()).encodeBuffer(key);
	}
	
	public static void main(String[] args) throws Exception{
		try{
			String pwd = "";
			if (args != null) {
				pwd = args[0];
			}
			if ("".equals(pwd)) {
				pwd = "chongqing";
			}
			/*String s = "{\"@class\":\"com.linkage.mapp.model.GXDatapackage\",\"header\":{\"@class\":\"com.linkage.mapp.model.GXHeader\",\"bizCode\":\"G0011\",\"identityId\":\"1376297910214\",\"respCode\":null,\"respMsg\":null,\"mode\":null},\"body\":{\"@class\":\"com.linkage.mapp.model.req.G0011Request\",\"key\":\"304050252938418\"}}";
			String key = "ailk_woyigou_server";

			String s_c = DESUtils.encrypt(s, key);
			
			s_c = new String(DESUtils.encryptBASE64(GZipUtils.compress(DESUtils.encrypt(s, key).getBytes("UTF-8"))));
//			msg = new String(DESUtils.encryptBASE64(GZipUtils.compress(DESUtils.encrypt(msg, serverKey).getBytes("UTF-8"))));
			System.out.println(s_c);
			
			s_c = "H4sIAAAAAAAAAKWRywFEQQQEU/IbzNFn5B/SejHsndJdyOdGAAJKFLpwkkhzgd/bPvWexNiJqdL7do6IGSPT2C8cfiPIYkqjVyVkOIwLS8krztHEy86D6HpaGVDvWX6KTGB1LIwcL05pNmtxwIhyB1K5NlTDEWfi15d0yoAUJd341RdC0ZLboSmLkFrn8gF9fMn9g/I743St2miWJ5ksEcjbpE1YVBnvKabsOt4iN7FOaB04FKW2900zNSV7i2PrXoPhY+ssrLffK1GCs8J4+pmfsbse3pJwV30NwStL0Wcg9HBHPSAB4Dw9n+ws7BKnP/+hHqGeetb3rkR5XFwK2gE+ZbPpUGuGi30N+huobESz9mABcN0vPrrAYwzeN3D8IsGXBn8egJK0MAIAAA==";
			
//			s_c = "H4sIAAAAAAAAAKWRyQEDMQjEWuIGPzlM/yXFW0P+gxgExZ5MQEDJxhAuEhluiHMmtu+VXNfcbjv35YiYMauc44DyXUEWN1o7JinL6dzYRtGpaoWHgxcxTMcY0I4+folsYk8+GAUe3LYatuaEFeNJpA4b6AGVYOI7h2zbgQylwvn2V8LQiydgqJqQxvawgl0+FPFB+eoGHe9x2seTKpZM5HfJuLCYMR5tpprWwJhVP0KbxzQtCNRk1VbrQk0Vjr1tsKz+nKXPu++22As+YbxzPXT9PA+3sNGq4hmC215i10Ho4otGQgGAXtNPdjVOS9Cf/7DIV7pMn+83kh158FHQFVjb97VD611ujmcw7kLXILpPJAtA2PvipQO8zhBzEjcOEnxt8AeJEnq8MAIAAA==";
			System.out.println(s_c);
			s_c = DESUtils.decrypt(new String(GZipUtils.decompress(DESUtils.decryptBASE64(new String(s_c.getBytes(),"utf-8")))),key);
			
			System.out.println("解密后："+s_c);*/
			System.out.println("加密前："+pwd);

			String hex = encrypt(pwd, "9527_BRYSJ");
			System.out.println("加密后："+hex);

		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
