/**
 * 
 */
package com.zengshi.butterfly.core.security.encrypt;

import java.security.Provider;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.zengshi.butterfly.core.config.Constants;
import com.zengshi.butterfly.core.util.ByteHexHelper;

/**
 * 
 */
public class DESEncrypt {
	
	private static Provider provider;
	static {
		String _provider=System.getProperty("security.provider");
		if(_provider != null && !"".equals(_provider.trim())) {
			try {
				Class c=Class.forName(_provider);
				provider=(Provider)c.newInstance();
			} catch (Exception e) {
				
			}
		}
		if(provider == null) {
			provider=new com.sun.crypto.provider.SunJCE();
		}
	}
	/**
	 * 使用DES加密
	 * @param algorithm 加密方法
	 * @param org 输入字符串
	 * @param desKey 加密key
	 * @param base64Wrapped 是否使用base64加密
	 * @return
	 */
	public static String encode(EncrpytEnum algorithm,String org,String desKey,boolean base64Wrapped) {
		try {
			Security.addProvider(provider);
			byte[] _desKey=null;
			if(base64Wrapped) {
				_desKey=new BASE64Decoder().decodeBuffer(desKey);
			}else {
				_desKey=ByteHexHelper.hexStr2ByteArray(desKey);
			}
			
			javax.crypto.spec.SecretKeySpec destmp =
				    new javax.crypto.spec.SecretKeySpec(_desKey,algorithm.name());
			Cipher c1 = Cipher.getInstance(algorithm.name());
			c1.init(Cipher.ENCRYPT_MODE, destmp);
			byte[] cipherByte = c1.doFinal(org.getBytes());
			
			if(base64Wrapped) {
				return new BASE64Encoder().encode(cipherByte);
			}else {
				return ByteHexHelper.bytesToHexString(cipherByte);
			}
			
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	/**
	 * DES解密
	 * @param algorithm
	 * @param encodedStr
	 * @param desKey
	 * @param base64Wrapped 是否使用base64对数据编码
	 * @return
	 */
	public static String decode(EncrpytEnum algorithm,String encodedStr,String desKey,boolean base64Wrapped) {		
		try {
			Security.addProvider(provider);
			byte[] _desKey=null;
			byte[] _encodeStr=null;
			if(base64Wrapped) {
				_desKey=new BASE64Decoder().decodeBuffer(desKey);
				_encodeStr=new BASE64Decoder().decodeBuffer(encodedStr);
			}else {
				_desKey=ByteHexHelper.hexStr2ByteArray(desKey);
				_encodeStr=ByteHexHelper.hexStr2ByteArray(encodedStr);
			}
			javax.crypto.spec.SecretKeySpec destmp =
				    new javax.crypto.spec.SecretKeySpec(_desKey,algorithm.name());
		
			Cipher c1 = Cipher.getInstance(algorithm.name());
			c1.init(Cipher.DECRYPT_MODE, destmp);
			byte[] clearByte = c1.doFinal(_encodeStr);
			return new String(clearByte);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}

		return null;

	}
	
	public static String encode(String src) {
		String key = Constants.DES_KEY.getValue();
		if (key == null) {
			key = "1ggCa8f0UkM=";
		}
		return encode(EncrpytEnum.DES,src,key,true);
	}
	
	public static String decode (String src) {
		String key = Constants.DES_KEY.getValue();
		if (key == null) {
			key = "1ggCa8f0UkM=";
		}
		return decode(EncrpytEnum.DES,src,key,true);
	}
	
	public static String MD5Encode(String src) {
		return encode(EncrpytEnum.HmacMD5,src,Constants.DES_KEY.getValue(),true);
	}

	public static void pwdGenerator(String[] arg) {
		System.out.println("=====原密码：" + arg[0] + "=====");

		System.out.println("-----加密后：" + encode(arg[0]) + "   ----");
		
	}
	
	public static void main(String[] arg) {
		String algorithm=EncrpytEnum.DES.name();
		String myDesKey=null;
		boolean base64Wrapped=true;
		try {
			KeyGenerator keygen = KeyGenerator.getInstance(EncrpytEnum.DES.name());
			
			SecretKey deskey = keygen.generateKey(); 
			byte[] desEncode=deskey.getEncoded(); 
			if(base64Wrapped) {
				myDesKey=new BASE64Encoder().encode(desEncode);
			}else {
				myDesKey=ByteHexHelper.bytesToHexString(desEncode);
			}
			
			
		} catch (Exception e) {
			
		}
		System.out.println("--------------"+myDesKey);
		String e=encode(EncrpytEnum.DES,"中国测试中234566",myDesKey,base64Wrapped);
		System.out.println(e);		
		
		System.out.println(decode(EncrpytEnum.DES,e,myDesKey,base64Wrapped));
		
		pwdGenerator(arg);
	}
}
