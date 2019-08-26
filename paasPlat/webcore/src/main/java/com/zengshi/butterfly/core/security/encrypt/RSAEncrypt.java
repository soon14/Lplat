package com.zengshi.butterfly.core.security.encrypt;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import java.security.KeyFactory;

import java.security.KeyPair;

import java.security.KeyPairGenerator;

import java.security.PrivateKey;

import java.security.PublicKey;

import java.security.interfaces.RSAPrivateKey;

import java.security.interfaces.RSAPublicKey;

import java.security.spec.PKCS8EncodedKeySpec;

import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.zengshi.butterfly.core.util.ByteHexHelper;

import sun.misc.BASE64Decoder;

import sun.misc.BASE64Encoder;

public class RSAEncrypt {

	private static final String ALGORITHM_RSA="RSA";
	/**
	 * 
	 * 得到公钥
	 * 
	 * @param key
	 *            密钥字符串（经过base64编码）
	 * 
	 * @throws Exception
	 */

	public static PublicKey getPublicKey(String key) throws Exception {

		byte[] keyBytes;

		keyBytes = (new BASE64Decoder()).decodeBuffer(key);

		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);

		PublicKey publicKey = keyFactory.generatePublic(keySpec);

		return publicKey;

	}

	/**
	 * 
	 * 得到私钥
	 * 
	 * @param key
	 *            密钥字符串（经过base64编码）
	 * 
	 * @throws Exception
	 */

	public static PrivateKey getPrivateKey(String key) throws Exception {

		byte[] keyBytes;

		keyBytes = (new BASE64Decoder()).decodeBuffer(key);

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);

		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

		return privateKey;

	}

	/**
	 * 
	 * 得到密钥字符串（经过base64编码）
	 * 
	 * @return
	 */

	public static String getKeyString(Key key) throws Exception {

		byte[] keyBytes = key.getEncoded();

		String s = (new BASE64Encoder()).encode(keyBytes);

		return s;

	}
	/**
	 * 经过base64二次赚吗的加密方法
	 * @param input 输入字符串
	 * @param publicKey 公钥字符串
	 * @return
	 */
	public static String encode(String input,String publicKey) {
		return encode(input,publicKey,true);
	}
	/**
	 * 
	 * @param input
	 * @param publicKey
	 * @param base64Wrapped
	 * @return
	 */
	public static String encode(String input,String publicKey,boolean base64Wrapped)  {
		 try {
			return  encode( input,getPublicKey(publicKey), base64Wrapped) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		 return null;
	}
	/**
	 * 
	 * @param input
	 * @param publicKey
	 * @return
	 */
	public static String encode(String input,Key publicKey) {
		return encode(input,publicKey,true);
	}
	/**
	 * 
	 * @param input
	 * @param publicKey
	 * @param base64Wrapped
	 * @return
	 */
	public static String encode(String input,Key publicKey,boolean base64Wrapped) {
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
			
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			
			
			byte[] enBytes = cipher.doFinal(input.getBytes());
			 
			if(base64Wrapped) {
				return new BASE64Encoder().encode(enBytes);
			}else {
				return ByteHexHelper.bytesToHexString(enBytes);
			}
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * @param input
	 * @param privateKey
	 * @return
	 */
	public static String decode(String input,Key privateKey) {
		return decode(input,privateKey,true);
	}
	/**
	 * 
	 * @param input
	 * @param privateKey
	 * @return
	 */
	public static String decode(String input,String privateKey) {
		return decode(input,privateKey,true);
	}
	/**
	 * 解密
	 * @param input 密文
	 * @param privateKey 解密私钥
	 * @param base64Wrapped 是否使用base64二次转码
	 * @return
	 */
	public static String decode(String input,Key privateKey,boolean base64Wrapped) {
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] _input=null;
			if(base64Wrapped) {
				_input=new BASE64Decoder().decodeBuffer(input);
			}else {
				_input=ByteHexHelper.hexStr2ByteArray(input);
			}
			byte[] deBytes = cipher.doFinal(_input);
			return new String(deBytes);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	/**
	 * 
	 * @param input
	 * @param privateKey
	 * @param base64Wrapped
	 * @return
	 */
	public static String decode(String input,String privateKey,boolean base64Wrapped) {
		try {
			return decode(input,getPrivateKey(privateKey),base64Wrapped);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param key
	 * @param base64Wrapped
	 * @return
	 */
	public static Key getKeyByString(String key,boolean base64Wrapped) {
		try {
			byte[] _publicKey=null;
			if(base64Wrapped) {
				_publicKey=new BASE64Decoder().decodeBuffer(key);
			}else {
				_publicKey=ByteHexHelper.hexStr2ByteArray(key);
			}
		
			javax.crypto.spec.SecretKeySpec destmp =
				    new javax.crypto.spec.SecretKeySpec(_publicKey,ALGORITHM_RSA);
			return destmp;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	/**
	 * 
	 * @param key
	 * @param base64Wrapped
	 * @return
	 */
	public static String getKey(Key key,boolean base64Wrapped) {
		byte[] keyBytes=key.getEncoded();
		if(base64Wrapped) {
			return new BASE64Encoder().encode(keyBytes);
		}else {
			return ByteHexHelper.bytesToHexString(keyBytes);
		}
	}
	
	public static void main(String[] args) {
		boolean base64Wrapped=true;
		try {
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM_RSA);

			// 密钥位数

			keyPairGen.initialize(1024);

			// 密钥对

			KeyPair keyPair = keyPairGen.generateKeyPair();
			
			//keyPairGen.

			// 公钥

			PublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
			//publicKey.getEncoded();

			// 私钥

			PrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
			//privateKey.get
			
			String out=encode("中国你好123123123123",publicKey,base64Wrapped);
			
			System.out.println(out);
			String result=decode(out,privateKey,base64Wrapped);
			System.out.println(result);
			
			System.out.println("====================");
			String strPubkey=getKey(publicKey, base64Wrapped);
			String strPriKey=getKey(privateKey,base64Wrapped);
			System.out.println("publicKey:\r\n"+strPubkey);
			System.out.println("privateKey:\r\n"+strPriKey);
			
			String out2=encode("中国你好123123123123",strPubkey,base64Wrapped);
			System.out.println(out2);
			String result2=decode(out,strPriKey,base64Wrapped);
			System.out.println(result2);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	public static String getKey(PrivateKey key,boolean base64Wrapped) {
		byte[] keyBytes=key.getEncoded();
		if(base64Wrapped) {
			return new BASE64Encoder().encode(keyBytes);
		}else {
			return ByteHexHelper.bytesToHexString(keyBytes);
		}
	}

}
