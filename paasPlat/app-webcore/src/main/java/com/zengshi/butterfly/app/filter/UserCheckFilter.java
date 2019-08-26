package com.zengshi.butterfly.app.filter;

import com.zengshi.ecp.server.front.dto.BaseStaff;
import com.zengshi.paas.utils.LogUtil;
import com.zengshi.butterfly.app.ActionConfig;
import com.zengshi.butterfly.app.ApplicationConfig;
import com.zengshi.butterfly.app.ErrorCodeDefine;
import com.zengshi.butterfly.app.MappContext;
import com.zengshi.butterfly.app.model.IAppDatapackage;
import com.zengshi.butterfly.core.exception.BusinessException;
import com.zengshi.butterfly.core.exception.DefaultExceptionCode;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserCheckFilter implements IFilter<Map<String,Object>> {

	//Logger logger = LoggerFactory.getLogger(UserCheckFilter.class);
    private static final String MODULE = UserCheckFilter.class.getName();
	
	@Override
	public void init() {
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public Map<String,Object> doFilter(Map<String,Object> context) throws Exception {
		
		IAppDatapackage request = (IAppDatapackage)context.get(MappContext.MAPPCONTEXT_REQUEST);
		
		ActionConfig config = ApplicationConfig.getActionConfig(request.getHeader().getBizCode());
		
		if(config == null)
			return context;
		
		if(config.isUserCheck() == false)
			return context;

		Object obj = context.get(MappContext.MAPPCONTEXT_USER);
		BaseStaff user = null;
		if(obj == null){
		} else{
			user = (BaseStaff)obj;
		}

		
		//用户信息未登录；
		if(user == null || user.getId() < 1){
		    
		    throw new BusinessException(new DefaultExceptionCode(ErrorCodeDefine.NO_USER_INFO,"APP 用户信息为 null"));
		}
		
		LogUtil.info(MODULE, "用户信息："+user.getId()+"; "+user.getStaffCode()+";"+user.getStaffLevelCode());
		
		return context;
	}

}
