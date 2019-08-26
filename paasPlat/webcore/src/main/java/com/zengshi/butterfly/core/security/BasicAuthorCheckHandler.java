package com.zengshi.butterfly.core.security;

import com.zengshi.butterfly.core.annotation.Security;
import com.zengshi.butterfly.core.web.RequestContext;
import com.zengshi.butterfly.core.web.security.AuthorException;
import com.zengshi.butterfly.core.web.security.AuthorUserPrincipal;
import com.zengshi.butterfly.core.web.security.AuthorizationResult;
import com.zengshi.butterfly.core.web.security.AuthorizationResult.ResultEnum;
import com.zengshi.butterfly.core.web.security.LoginInfoHelper;

public class BasicAuthorCheckHandler implements SecurityCheckHandler{

	private int order=Integer.MIN_VALUE;
	
	@Override
	public boolean isPermission(Security security) throws Exception {
		if(security.mustLogin()) {
			//校验用户是否登录
			AuthorUserPrincipal authorPrincipal=LoginInfoHelper.get();
			
			this.checkLogin(authorPrincipal);
			this.checkSessionExpired(authorPrincipal);
			this.checkClientIp(authorPrincipal);
			this.checkUserName(authorPrincipal,security.userName());
			this.checkUserRole(authorPrincipal,security.role());
		
			
		}
		return true;
	}
	
	/**
	 * 校验是否已经登录
	 * @param user
	 * @throws AuthorException
	 */
	protected void checkLogin(AuthorUserPrincipal user)  throws AuthorException{
		if(user == null) {
			AuthorizationResult authorResult=new AuthorizationResult();
			authorResult.setResult(ResultEnum.NOT_LOGIN);
			throw new AuthorException(authorResult);
		}
	}
	
	/**
	 * 如果限定了用户名，则必须是指定的用户名才可以访问
	 * @param user
	 * @param userName
	 * @throws AuthorException
	 */
	protected void checkUserName(AuthorUserPrincipal user,String[] userName)  throws AuthorException{
		if(userName != null && userName.length > 0) {
			boolean hasUser=false;
			for(String name:userName) {
				if(name.equals(user.getLoginName())) {
					hasUser=true;
					break;
				}
			}
			AuthorizationResult authorResult=new AuthorizationResult();
			authorResult.setResult(ResultEnum.NOT_PERMIT);
			if(!hasUser) throw new AuthorException(authorResult);
		}
	}
	
	protected void checkSessionExpired(AuthorUserPrincipal authorPrincipal)  throws AuthorException{
		if(authorPrincipal.isExpired()) {
			AuthorizationResult authorResult=new AuthorizationResult();
			authorResult.setResult(ResultEnum.EXPIRED);
			throw new AuthorException(authorResult);
		}
	}
	
	/**
	 * 检查访问的ip是否来自授权的ip
	 * @param authorPrincipal
	 * @throws AuthorException
	 */
	protected void checkClientIp(AuthorUserPrincipal authorPrincipal)  throws AuthorException{
		if(!authorPrincipal.getClientIP().equals(RequestContext.getRequest().getRemoteAddr())) {
			AuthorizationResult authorResult=new AuthorizationResult();
			authorResult.setResult(ResultEnum.NOT_PERMIT);
			throw new AuthorException(authorResult);
		}
	}
	
	
	/**
	 * 子类扩展用户角色
	 * @param roles
	 * @param user
	 * @throws AuthorException
	 */
	protected void checkUserRole(AuthorUserPrincipal user,String[] roles) throws AuthorException {
		
	}
	

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

}
