package com.zengshi.paas.security.signature;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.zengshi.paas.ConfigurationCenter;
import com.zengshi.paas.ConfigurationWatcher;
import com.zengshi.paas.PaasException;
import com.zengshi.paas.file.FileManagerSVC;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 签名服务管理器
 *
 */
public class SignatureSVCManager implements ConfigurationWatcher {
	public static final Logger log = Logger.getLogger(SignatureSVCManager.class);
	
	private FileManagerSVC secureFileRepo = null;
	private String confPath = "/com/zpaas/secure/signatureSVCManager";
	private ConfigurationCenter cc = null;
	private String conf = null;
	private HashMap<Long, SignatureSVC> signMap = new HashMap<Long, SignatureSVC>();
	
	public void init() {
		try {
			process(cc.getConfAndWatch(confPath, this));
			
		} catch (PaasException e) {
			log.error(e.getMessage(),e);
		}
	}
	
	public void process(String conf) {
		JSONArray json = JSONArray.fromObject(conf);
		if(conf != null && json.size() > 0 && !conf.equals(this.conf)) {
			this.conf = conf;
			int size = json.size();
			for(int i=0; i<size; i++) {
				JSONObject obj = json.getJSONObject(i);
				Long acctId = obj.getLong("acctId");
				String signatureConf = obj.getString("signatureConf");
				String signatureImpl = obj.getString("signatureImpl");
				SignatureSVC util = null;
				try {
					Class<?> clazz = Class.forName(signatureImpl);
					util = (SignatureSVC)clazz.newInstance();
					util.init(signatureConf, secureFileRepo);
				} catch (Exception e) {
					log.error(e.getMessage(),e);
					log.error("init signature failed:" + e);
				} 
				signMap.put(acctId, util);
			}
		}
	}
	
	public String sign(String plainText, String charset, Long merchantAcctId) {
		SignatureSVC sign = signMap.get(merchantAcctId);
		if(sign != null) {
			return sign.sign(plainText, charset);
		}
		return null;
	}
	
	public String sign(String plainText, Long merchantAcctId) {
		SignatureSVC sign = signMap.get(merchantAcctId);
		if(sign != null) {
			return sign.sign(plainText);
		}
		return null;
	}
	
	public boolean verifySignature(String sign, String plainText, Long merchantAcctId) {
		SignatureSVC signUtil = signMap.get(merchantAcctId);
		if(signUtil != null) {
			return signUtil.verifySignature(sign, plainText);
		}
		return false;
	}
	
	public boolean verifySignatureWithCharset(String sign, String plainText, String charset, Long merchantAcctId) {
		SignatureSVC signUtil = signMap.get(merchantAcctId);
		if(signUtil != null) {
			return signUtil.verifySignatureWithCharset(sign, plainText, charset);
		}
		return false;
	}
	
	public boolean verifySignature(String sign, String plainText, String cerFile, Long merchantAcctId) {
		SignatureSVC signUtil = signMap.get(merchantAcctId);
		if(signUtil != null) {
			return signUtil.verifySignature(sign, plainText, cerFile);
		}
		return false;
	}
	
	public boolean verifySignatureWithCharset(String sign, String plainText, String cerFile, String charset, Long merchantAcctId) {
		SignatureSVC signUtil = signMap.get(merchantAcctId);
		if(signUtil != null) {
			return signUtil.verifySignatureWithCharset(sign, plainText, cerFile,charset);
		}
		return false;
	}
	
	
	public FileManagerSVC getSecureFileRepo() {
		return secureFileRepo;
	}

	public void setSecureFileRepo(FileManagerSVC secureFileRepo) {
		this.secureFileRepo = secureFileRepo;
	}

	public String getConfPath() {
		return confPath;
	}

	public void setConfPath(String confPath) {
		this.confPath = confPath;
	}

	public ConfigurationCenter getCc() {
		return cc;
	}

	public void setCc(ConfigurationCenter cc) {
		this.cc = cc;
	}

	public static void main(String[] args) {

	}
}
