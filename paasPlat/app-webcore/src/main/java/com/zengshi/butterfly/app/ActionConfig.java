/**
 * 
 */
package com.zengshi.butterfly.app;

import com.zengshi.butterfly.app.base.BaseActionHandler;
import com.zengshi.butterfly.app.model.IBody;

/**
 * @modify mler 增加ip，right校验属性
 *
 */
public class ActionConfig {

	private String bizCode;
	
	private Class<? extends BaseActionHandler> actionType;
	
	private Class<? extends IBody> requestType;
	
	private Class<? extends IBody> responseType;
	
	private boolean userCheck;
	
	private boolean rightCheck;
	
	private boolean ipCheck;

	public ActionConfig(String bizCode,
			Class<? extends BaseActionHandler> actionType,
			Class<? extends IBody> requestType,
			Class<? extends IBody> responseType, boolean userCheck,
			boolean rightCheck, boolean ipCheck) {
		super();
		this.bizCode = bizCode;
		this.actionType = actionType;
		this.requestType = requestType;
		this.responseType = responseType;
		this.userCheck = userCheck;
		this.rightCheck = rightCheck;
		this.ipCheck = ipCheck;
	}
	
	public ActionConfig(String bizCode,
			Class<? extends BaseActionHandler> actionType,
			Class<? extends IBody> requestType,
			Class<? extends IBody> responseType) {
		super();
		this.bizCode = bizCode;
		this.actionType = actionType;
		this.requestType = requestType;
		this.responseType = responseType;
	}



	public String getBizCode() {
		return bizCode;
	}

	public void setBizCode(String bizCode) {
		this.bizCode = bizCode;
	}

	public Class<? extends BaseActionHandler> getActionType() {
		return actionType;
	}

	public void setActionType(Class<? extends BaseActionHandler> actionType) {
		this.actionType = actionType;
	}

	public Class<? extends IBody> getRequestType() {
		return requestType;
	}

	public void setRequestType(Class<? extends IBody> requestType) {
		this.requestType = requestType;
	}

	public Class<? extends IBody> getResponseType() {
		return responseType;
	}

	public void setResponseType(Class<? extends IBody> responseType) {
		this.responseType = responseType;
	}

	public boolean isUserCheck() {
		return userCheck;
	}

	public void setUserCheck(boolean userCheck) {
		this.userCheck = userCheck;
	}

	public boolean isRightCheck() {
		return rightCheck;
	}

	public void setRightCheck(boolean rightCheck) {
		this.rightCheck = rightCheck;
	}

	public boolean isIpCheck() {
		return ipCheck;
	}

	public void setIpCheck(boolean ipCheck) {
		this.ipCheck = ipCheck;
	}
	
}
