package com.zengshi.butterfly.core.security.encrypt;

import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;

import com.zengshi.butterfly.core.util.ByteHexHelper;

public class Encrypt {

	public static String MD5="MD5";
	public static String SHA1="SHA-1";
	//public static String BASE64="BASE64";
	public static String  encode(String org,String encrypt,boolean base64Wrapped)  {
		try {
			java.security.MessageDigest alga = java.security.MessageDigest
					.getInstance(encrypt);
			alga.update(org.getBytes());
			byte[] digesta = alga.digest();
			if(base64Wrapped) {
				return new BASE64Encoder().encode(digesta);
			}else {
				return ByteHexHelper.bytesToHexString(digesta);
			}
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void main(String[] arg) {
		String p=Encrypt.encode("1", Encrypt.MD5,false);
		//edfbddef5c15ac89cbc71da297f3c467
		System.out.println(p);
		
	}
}
