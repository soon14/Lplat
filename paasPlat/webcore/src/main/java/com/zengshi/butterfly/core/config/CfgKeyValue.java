package com.zengshi.butterfly.core.config;


public class  CfgKeyValue {

	protected String key;
	
	public CfgKeyValue(String key) {
		this.key=key;
	}
	
	public String getValue() {
		return this.getValue(key);
	}
	
	/**
	 * 优先Application所保存的数据
	 * @param key
	 * @return
	 */
	protected String getValue(String key) {
		try {
			String value=Application.getValue(key);
			if(value != null  && !"".equals(value)) {
				return value;
			}
			ConfigContainer container=ConfigContainerDefault.getInstance();
			return (String)container.findConfig(key).getValue();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public String getKey() {
		return this.key;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getValue();
	}
	
	
}
