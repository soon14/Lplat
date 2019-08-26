package com.zengshi.butterfly.core.web.security;

public interface AuthorUserPrincipal {

	public Long getUserId();

	/**
	 * 用户ID
	 * 
	 * @param userId
	 */
	public void setUserId(Long userId);

	/**
	 * 登入ID
	 * 
	 * @return loginId
	 */
	public String getLoginId();

	/**
	 * 登入ID
	 * 
	 * @param loginId
	 */
	public void setLoginId(String loginId);
	
	public String getLoginName();

	public void setLoginName(String loginName) ;
	

	/**
	 * 用户昵称
	 * 
	 * @return nickName
	 */
	public String getNickName();

	/**
	 * 用户昵称
	 * 
	 * @param nickName
	 */
	public void setNickName(String nickName);

	/**
	 * 登录ID类型(0:邮箱，1:手机)
	 * 
	 * @return loginIdType
	 */
	public Integer getLoginIdType();

	/**
	 * 登录ID类型(0:邮箱，1:手机)
	 * 
	 * @param loginIdType
	 */
	public void setLoginIdType(Integer loginIdType);

	public String getClientIP();

	public void setClientIP(String clientIP);

	public String getSessionId();

	public void setSessionId(String sessionId);

	public long getModifyTime();

	public void setModifyTime(long modifyTime);

	// 如果10分钟没有操作，则失效
	public boolean isExpired();

	public void setExpired(boolean expired);

}
