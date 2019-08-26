package com.zengshi.butterfly.core.security.encrypt;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Provider;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtil {
	private static final String ALGORITHM_DES = "DES";
	private static final String ENCODING_UTF8 = "UTF8";

	/**
	 * This function encrypt a given string using the DES algorithm.
	 * 
	 * @param strDataToEncrypt
	 *            The String to encrypt
	 * @param strKey
	 *            The generated key used to encrypt
	 * @return The encrypted string
	 */
	public static String encrypt(String strDataToEncrypt, String strKey) {
		byte[] key = strKey.getBytes();

		// Get the KeyGenerator
		Provider sunJCE = new com.sun.crypto.provider.SunJCE();
		Security.addProvider(sunJCE);

		String strAlgorithm = ALGORITHM_DES; // On utilise un algorithme DES
		SecretKeySpec keySpec = null;
		DESKeySpec deskey = null;
		String strResult = "";

		try {
			// Prepare the key
			deskey = new DESKeySpec(key);
			keySpec = new SecretKeySpec(deskey.getKey(), ALGORITHM_DES);

			// Instantiate the cipher
			Cipher cipher = Cipher.getInstance(strAlgorithm);

			// Encrypt data
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);

			// Encode the string into bytes using utf-8
			byte[] utf8 = strDataToEncrypt.getBytes(ENCODING_UTF8); // FIXME ?

			// Encrypt
			byte[] enc = cipher.doFinal(utf8);

			// Encode bytes to base64 to get a string
			strResult = new sun.misc.BASE64Encoder().encode(enc);
		} catch (Exception e) {
			//AppLogService.error("Data encryption error", e);
		}

		return strResult;
	}

	/**
	 * This function decrypt a given string using the DES algorithm.
	 * 
	 * @param strDataToDecrypt
	 *            The String to decrypt
	 * @param strKey
	 *            The generated key used to decrypt
	 * @return The encrypted string
	 * @throws UnsupportedEncodingException 
	 */
	public static String decrypt(String strDataToDecrypt, String strKey) {
		byte[] key = strKey.getBytes();

		// Get the KeyGenerator
		Provider sunJCE = new com.sun.crypto.provider.SunJCE();
		Security.addProvider(sunJCE);

		String strAlgorithm = ALGORITHM_DES;
		SecretKeySpec keySpec = null;
		DESKeySpec deskey = null;
		String strResult = "";

		try {
			// Prepare the key
			deskey = new DESKeySpec(key);
			keySpec = new SecretKeySpec(deskey.getKey(), ALGORITHM_DES);

			// Instantiate the cipher
			Cipher cipher = Cipher.getInstance(strAlgorithm);
			cipher.init(Cipher.DECRYPT_MODE, keySpec);

			// Decrypt data
			// Decode base64 to get bytes
			byte[] dec = new sun.misc.BASE64Decoder()
					.decodeBuffer(strDataToDecrypt);

			// Decrypt
			byte[] utf8 = cipher.doFinal(dec);

			// Decode using utf-8
			return new String(utf8, ENCODING_UTF8);
		}

		catch (Exception e) {
			//AppLogService.error("Data decryption error", e);
			e.printStackTrace();
		}

		return strResult;
	}

	public static void main(String[] arg) throws UnsupportedEncodingException {
		String value=CryptoUtil.encrypt(URLEncoder.encode("中国", "UTF-8"), "#$ewiuewd");
		System.out.println(value);
		String uvalue=CryptoUtil.decrypt(value, "#$ewiuewd");
		System.out.println(URLDecoder.decode(uvalue, "UTF-8") );
	}
}
