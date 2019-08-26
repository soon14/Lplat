package com.zengshi.butterfly.core.examples.config;

import com.zengshi.butterfly.core.config.Config;
import com.zengshi.butterfly.core.config.ConfigContainer;
import com.zengshi.butterfly.core.config.ConfigContainerDefault;
import com.zengshi.butterfly.core.config.center.CenterPriorityEnum;
import com.zengshi.butterfly.core.config.provider.ConfigProvider;
import com.zengshi.butterfly.core.config.provider.PropertyConfigProvider;

public class PropertyConfigExample {

	public static void main(String[] arg) {
		ConfigProvider provider=new PropertyConfigProvider("com/zengshi/core/examples/config/demo.properties");
		
		try {
			ConfigContainer container=ConfigContainerDefault.getInstance();
			provider.init(container, CenterPriorityEnum.DEV);
			provider.registerToCenter();
			
			UserInfo user=container.injectConfig(UserInfo.class);
			
			Config config=container.findConfig("username");
			
			
			System.out.println("end===" +(config == null?"未设置":config.getValue()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//provider.i
	}
}
