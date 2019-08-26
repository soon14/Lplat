package com.zengshi.paas;

/**
 * 接入统一配置中心的接口类
 *
 */
public interface ConfigurationWatcher {
	public void process(String conf);
}
