package com.zengshi.butterfly.app.filter;

import com.zengshi.butterfly.app.ActionConfig;
import com.zengshi.butterfly.app.ApplicationConfig;
import com.zengshi.butterfly.app.ErrorCodeDefine;
import com.zengshi.butterfly.app.MappContext;
import com.zengshi.butterfly.app.model.IAppDatapackage;
import com.zengshi.butterfly.core.exception.BusinessException;
import com.zengshi.butterfly.core.exception.DefaultExceptionCode;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class IpCheckFilter implements IFilter<Map<String,Object>> {

	Logger logger = LoggerFactory.getLogger(IpCheckFilter.class);
	
	private static String IP_SPLIT_CHAR = ",";
	
	//@Value("${VALID_IP}")
	///这部分的注解先不需要；
	private String validIP = "";
	
	private Set<String> valid_IP_Stack;
	
	@Override
	@PostConstruct
	public void init() throws Exception {
		try
		{
			if(StringUtils.isBlank(validIP) == false)
			{
				valid_IP_Stack = new HashSet<String>(0);
				
				String[] ip_list = validIP.split(IP_SPLIT_CHAR);
				for(String ip : ip_list)
				{
					if(StringUtils.isBlank(ip))
						continue;
					valid_IP_Stack.add(ip);
				}
			}
		}catch (Exception e) {
			logger.error("初始化载入 VALID_IP 出错。");
			throw new BusinessException(new DefaultExceptionCode(ErrorCodeDefine.EXPECT_ERROR, "初始化载入validIP出错。"), e);
		}
		
		logger.debug("VALID_IP 初始化完成");
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String,Object> doFilter(Map<String,Object> context) throws Exception {
		
		String requestIP = (String) context.get(MappContext.MAPPCONTEXT_REQUEST_IP);
		
		logger.debug("请求IP:"+requestIP);
		
		IAppDatapackage request = (IAppDatapackage)context.get(MappContext.MAPPCONTEXT_REQUEST);
		
		ActionConfig config = ApplicationConfig.getActionConfig(request.getHeader().getBizCode());
		
		if(config == null)
			return context;
		
		if(config.isIpCheck() == false || valid_IP_Stack == null || valid_IP_Stack.isEmpty())
			return context;
		
		if(valid_IP_Stack.contains("*")) {
			
		} else {
			if(valid_IP_Stack.contains(requestIP) == false)
				throw new BusinessException(new DefaultExceptionCode(ErrorCodeDefine.EXPECT_ERROR,"未被允许的IP请求，IP:"+requestIP));
		}
		return context;

	}

	public void setValidIP(String validIP) {
		this.validIP = validIP;
	}
	
	

}
