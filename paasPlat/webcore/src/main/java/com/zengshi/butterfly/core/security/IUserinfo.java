package com.zengshi.butterfly.core.security;


/**
 * 用户信息基本接口
 *
 */
public interface IUserinfo {

	public void setUserId(Integer userId);
	
	public Integer getUserId();
	
	public void setUserName(String userName);
	
	public String getUserName();
	
	public String getNickName();
	
	public String getMailBox();
	
	public Integer getLogoId();
	
	/**
	 * 获取用户设置变量值
	 * @param groupName 分组名
	 * @param key 变量KEY
	 * @return 若找到匹配的变量，则返回变量值，否则返回null
	 */
	public String getUserSetting(String groupName,String key);
		
	/**
	 * 非持久化的更新用户的设置，(只存在于对象lifecycle)
	 * @param groupname
	 * @param key
	 * @param value
	 */
	public void updateUserSetting(String groupname, String key, String value);
}
