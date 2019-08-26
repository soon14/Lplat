package com.zengshi.paas.cache.local;

import org.apache.log4j.Logger;

import com.zengshi.paas.ConfigurationCenter;
import com.zengshi.paas.ConfigurationWatcher;
import com.zengshi.paas.PaasException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 本地缓存服务类
 *
 */
public class LocalCacheSVCImpl implements ConfigurationWatcher,LocalCacheSVC {

	private static final Logger log = Logger.getLogger(LocalCacheSVCImpl.class);
		
	private JSONObject cache = null;
	private ConfigurationCenter cc = null;
	private String confPath = "/com/zengshi/paas/cache/local/localCacheSVC";
	
	public LocalCacheSVCImpl() {
		
	}
	
	public void init() {
		try {
			process(cc.getConfAndWatch(confPath, this));
			
		} catch (PaasException e) {
			e.printStackTrace();
		}
	}
	
	public void process(String conf) {
		if(log.isInfoEnabled()) {
			log.info("new cache information is received: " + conf);
		}
		if(conf != null && conf.trim().length() > 0) {
			cache = JSONObject.fromObject(conf);
		}
		
	}
	
	public Object getCacheItem(String key) {
		return cache.get(key);
	}
	
	public String getCacheItemString(String key) {
		return cache.getString(key);
	}
	
	public JSONObject getCacheItemJSONObject(String key) {
		return cache.getJSONObject(key);
	}
	
	public JSONArray getCacheItemJSONArray(String key) {
		return cache.getJSONArray(key);
	}

	public ConfigurationCenter getCc() {
		return cc;
	}

	public void setCc(ConfigurationCenter cc) {
		this.cc = cc;
	}

	public String getConfPath() {
		return confPath;
	}

	public void setConfPath(String confPath) {
		this.confPath = confPath;
	}
}
