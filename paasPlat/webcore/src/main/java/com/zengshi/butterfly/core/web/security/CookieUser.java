package com.zengshi.butterfly.core.web.security;

import javax.servlet.http.HttpServletRequest;


import com.zengshi.butterfly.core.util.RandomValueStringGenerator;

public class CookieUser implements AuthorUserPrincipal{
    // 用户ID
    private Long userId;
    // 登入ID
    private String loginId;
    
    private String loginName;
    // 用户昵称
    private String nickName;
    
    // 登录ID类型(0:邮箱，1:手机)
    private Integer loginIdType;
    
    //客户端登录ip
    private String clientIP;
    
    private String sessionId;
    
    private long modifyTime;
    
    private boolean expired;
    
    
    public CookieUser() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CookieUser(HttpServletRequest request) {
		super();
		this.clientIP=request.getRemoteAddr();
		RandomValueStringGenerator generator=new RandomValueStringGenerator(18,RandomValueStringGenerator.CODEC_ALL);
		this.sessionId=generator.generate();
		modifyTime=System.currentTimeMillis();
		expired=false;
	}
	
	/**
     * 用户ID
     * 
     * @return userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 用户ID
     * 
     * @param userId
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 登入ID
     * 
     * @return loginId
     */
    public String getLoginId() {
        return loginId;
    }

    /**
     * 登入ID
     * 
     * @param loginId
     */
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    /**
     * 用户昵称
     * 
     * @return nickName
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * 用户昵称
     * 
     * @param nickName
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


    /**
     * 登录ID类型(0:邮箱，1:手机)
     * 
     * @return loginIdType
     */
    public Integer getLoginIdType() {
        return loginIdType;
    }

    /**
     * 登录ID类型(0:邮箱，1:手机)
     * 
     * @param loginIdType
     */
    public void setLoginIdType(Integer loginIdType) {
        this.loginIdType = loginIdType;
    }

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public long getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(long modifyTime) {
		this.modifyTime = modifyTime;
	}

	//如果10分钟没有操作，则失效
	public boolean isExpired() {
		//return (System.currentTimeMillis()-this.getModifyTime()) > 300*1000 || this.expired;
		return false;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	
	
	
    
}
