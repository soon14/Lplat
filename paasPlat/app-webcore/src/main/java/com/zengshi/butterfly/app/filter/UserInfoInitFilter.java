package com.zengshi.butterfly.app.filter;

import com.zengshi.ecp.base.util.AppUserCacheUtils;
import com.zengshi.ecp.server.front.dto.BaseStaff;
import com.zengshi.ecp.server.front.util.StaffLocaleUtil;
import com.zengshi.paas.utils.LogUtil;
import com.zengshi.butterfly.app.MappContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserInfoInitFilter implements IFilter<Map<String,Object>> {

    private static final String MODULE = UserInfoInitFilter.class.getName();
	
	@Override
	public void init() {
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public Map<String,Object> doFilter(Map<String,Object> context) throws Exception {
	    
	    LogUtil.debug(MODULE,"============== 从缓存中获取用户信息开始 ============= ");
	    
	    BaseStaff staffInfo = null;

	    Object sessionKey = context.get(MappContext.MAPPCONTEXT_SESSIONID);

        if (sessionKey != null) {
            String sessionId = String.valueOf(sessionKey);
            // 需要从缓存中获取用户信息；
            try {
                staffInfo = AppUserCacheUtils.fetchUser(sessionId);
            } catch (Exception e) {
                LogUtil.error(MODULE,"从缓存中获取用户信息异常，objectId : "+sessionId, e);
            }
            
            if (staffInfo != null) {
                StaffLocaleUtil.setStaff(staffInfo);
            }
            
            context.put(MappContext.MAPPCONTEXT_USER,staffInfo);
        } else {
            
            context.put(MappContext.MAPPCONTEXT_USER,null);
        }
        
        if(staffInfo == null){
            LogUtil.info(MODULE, "用户信息为空");
        } else {
            LogUtil.info(MODULE, "用户信息："+staffInfo.getId()+"; "+staffInfo.getStaffCode()+";"+staffInfo.getStaffLevelCode());
        }
		
		return context;
	}

}
