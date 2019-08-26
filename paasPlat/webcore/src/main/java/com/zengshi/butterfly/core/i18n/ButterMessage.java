/**
 * 
 */
package com.zengshi.butterfly.core.i18n;

import java.util.Locale;

import com.zengshi.paas.utils.LocaleUtil;
import com.zengshi.butterfly.core.config.CfgKeyValue;

/**
 *
 */
public class ButterMessage extends CfgKeyValue {

	private String key;
	
	private String value;
	
	public ButterMessage(String key) {
		super(key);
		this.key=key;
		String localKey=getLocalKey();
		this.value=super.getValue(localKey);
		if(this.value == null || "".equals(this.value.trim())) {
			this.key=key;
			this.value=super.getValue(this.key);
			if(this.value == null || "".equals(this.value.trim())) {
				this.value=this.key;
			}
		}
	}
	
	private String getLocalKey() {
		Locale local= LocaleUtil.getLocale();
		if(local == null) {
			String _loca=System.getProperty("system.location");
			if(_loca!= null && !"".equals(_loca.trim())) {
				local=new Locale(_loca);
			}
		}
		if(local == null) {
			local=Locale.getDefault();
		}
		
		return super.getKey()+"."+local.toString();
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public String getValue() {
		/*String value=super.getValue(this.getKey());
		if(value == null || "".equals(value)) {
			value= this.getValue(this.key);
		}
		if(value ==  null  || "".equals(value)) {
			value=this.key;
		}
		return value;*/
		
		return this.value;
	}

}
