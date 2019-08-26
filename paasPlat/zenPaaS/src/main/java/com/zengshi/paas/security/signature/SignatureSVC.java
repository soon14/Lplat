package com.zengshi.paas.security.signature;

import com.zengshi.paas.file.FileManagerSVC;

/**
 * 签名服务接口类
 *
 */
public interface SignatureSVC {
	public String sign(String plainText, String charset);
	
	public String sign(String plainText);
	
	public boolean verifySignature(String sign, String plainText);
	
	public boolean verifySignatureWithCharset(String sign, String plainText, String charset);
	
	public boolean verifySignature(String sign, String plainText, String cerFile);
	
	public boolean verifySignatureWithCharset(String sign, String plainText, String cerFile, String charset);
	
	public void init(String conf,FileManagerSVC secureFileRepo);
}
