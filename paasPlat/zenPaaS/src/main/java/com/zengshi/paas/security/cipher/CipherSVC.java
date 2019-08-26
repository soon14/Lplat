package com.zengshi.paas.security.cipher;

/**
 * 加密服务接口类
 *
 */
public interface CipherSVC {
	public String encrypt(String data);	
	public String decrypt(String data);
}
