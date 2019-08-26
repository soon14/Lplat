/**
 * 
 */
package com.zengshi.butterfly.core;

import java.io.Serializable;

/**
 * 方法调用返回结果
 */
public class DefaultResult<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7650342589093599419L;
	/**
	 * 该方法调用是否成功
	 */
	protected boolean invokeSuccess = true;
	/**
	 * 调用结果代码（成功代码默认为200， 错误代码由各个模块自己定义）
	 */
	protected String resultCode;
	protected final String successCode = "200";//成功的代码
	/**
	 * 调用结果信息（一般是错误信息）
	 */
	protected String resultMsg;
	/**
	 * ?
	 */
	private Throwable e;
	/**
	 * 调用结果数据（比如查询结果列表）
	 */
	protected T resultValue;
	
	public DefaultResult() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 全参构造方法，不建议使用(参数容易 设置冲突。)
	 * @param invokeSuccess
	 * @param resultCode
	 * @param resultMsg
	 * @param resultValue
	 */
	public DefaultResult(boolean invokeSuccess,String resultCode ,String resultMsg,T resultValue){
		this.invokeSuccess = invokeSuccess;
		this.resultCode = resultCode;
		this.resultMsg = resultMsg;
		this.resultValue = resultValue;
	}
	
	/**
	 * 工厂方法，构造一个调用失败的结果
	 * @param errorCode
	 * @param errorMsg
	 * @return
	 */
	public static  DefaultResult<Boolean> errorResult(String errorCode ,String errorMsg){
		return new DefaultResult<Boolean>(false, errorCode, errorMsg, null);
	}
	
	/**
	 * 工厂方法，构造一个调用失败的结果
	 * @param errorCode
	 * @param errorMsg
	 * @return
	 */
	public static  DefaultResult<Boolean> unknowError(String errorMsg){
		return new DefaultResult<Boolean>(false, "400", errorMsg, null);
	}
	
	/**
	 * 工厂方法，构造一个调用失败的结果
	 * @param errorCode
	 * @param errorMsg
	 * @return
	 */
	public static  DefaultResult<Boolean> success(String msg){
		return new DefaultResult<Boolean>(true, "200", msg, null);
	}
	
	
	
	/**
	 * 构造方法,调用成功的时候使用该方法
	 * @param resultValue 调用结果数据
	 */
	public DefaultResult(T resultValue){
		this.invokeSuccess = true;// 成功
		this.resultCode = this.successCode;//默认200
		this.resultValue = resultValue;//调用结果数据
	}
	

	
	/**
	 * 方法是否调用成功
	 * @return
	 */
	public boolean isInvokeSuccess() {
		return invokeSuccess;
	}
	/**
	 * 获取方法调用结果代码
	 * @return
	 */
	public String getResultCode() {
		return resultCode;
	}
	/**
	 * 获取方法调用结果 信息（一般是错误信息）
	 * @return
	 */
	public String getResultMsg() {
		return resultMsg;
	}
	/**
	 * 获取方法调用
	 * @return
	 */
	public T getResultValue() {
		return resultValue;
	}

	@Override
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("invokeSuccess:").append(this.invokeSuccess)
		.append(";resultCode:").append(this.resultCode)
		.append(";resultMsg:").append(this.resultMsg);
		if(this.e  != null) {
			sb.append("\r\nException stack infomation: \r\n").append(e);
		}
		sb.append("\r\nresult:").append(this.resultValue == null?"null":this.resultValue.toString());
		return super.toString();
	}

	
	
}
