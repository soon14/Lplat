/**
 * 
 */
package com.zengshi.butterfly.app.base;

import java.lang.reflect.ParameterizedType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.zengshi.butterfly.app.ErrorCodeDefine;
import com.zengshi.butterfly.app.MappContext;
import com.zengshi.butterfly.app.annotation.Request;
import com.zengshi.butterfly.app.annotation.Response;
import com.zengshi.butterfly.app.interfaces.IHandler;
import com.zengshi.butterfly.app.model.IBody;
import com.zengshi.butterfly.core.exception.BusinessException;
import com.zengshi.butterfly.core.exception.SystemException;

/**
 * 
 */
public abstract class BaseActionHandler<Req extends IBody, Rsp extends IBody> implements IHandler, InitializingBean {

	private static final Logger logger = Logger.getLogger(BaseActionHandler.class);

	@Request
	protected Req request;

	@Response
	protected Rsp response;

	public BaseActionHandler() {
		super();
	}

	@SuppressWarnings("unchecked")
	private void initActionHandler() throws Exception {
		/**
		 * 初始化请求报文
		 */
		this.setRequest((Req) MappContext.getRequest().getBody());

		/**
		 * 响应报文报体初始化
		 */
		this.createResponseBody();

	}

	public void doHandle() throws Exception 
	{
		try {

			initActionHandler();

			this.doAction();

			MappContext.getResponse().setBody(this.getResponse());
			MappContext.getResponse().getHeader().setRespCode(ErrorCodeDefine.SUCCESS);
			MappContext.getResponse().getHeader().setRespMsg("");

		} catch (BusinessException e) {
			logger.error(e.getErrorCode()+":"+e.getErrorMsg());
			MappContext.getResponse().getHeader().setRespCode(e.getErrorCode());
			MappContext.getResponse().getHeader().setRespMsg(e.getErrorMsg());

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			MappContext.getResponse().getHeader().setRespCode(e.getErrorCode());
			MappContext.getResponse().getHeader().setRespMsg(e.getErrorMsg());

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			MappContext.getResponse().getHeader().setRespCode(ErrorCodeDefine.UNKNOW_ERROR);
			MappContext.getResponse().getHeader().setRespMsg(e.getMessage());

		} finally {
			try {
				onActionComplete();
			} catch (Exception e2) {
				logger.error(e2.getMessage(), e2);
			}
		}
	}

	protected abstract void doAction() throws BusinessException,
			SystemException, Exception;

	private void onActionComplete() throws BusinessException, SystemException,
			Exception {
		this.request = null;
		this.response = null;
	};

	public void afterPropertiesSet() throws Exception {
		logger.debug("afterPropertiesSet 方法");
	}

	private Rsp getResponse() {
		return this.response;
	}

	private void setRequest(Req request) {
		this.request = request;
	}

	@SuppressWarnings("unused")
	private void setResponse(Rsp response) {
		this.response = response;
	}

	@SuppressWarnings("unused")
	private Req getRequeset() {
		return this.request;
	}

	private Class<?> getResponseClass() throws Exception {
		logger.debug(((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[1]);
		return (Class<?>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[1];
	}

	private Class<?> getRequestClass() throws Exception {
		//System.out.println(getClass().getGenericSuperclass());
		return (Class<?>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@SuppressWarnings("unchecked")
	private void createResponseBody() throws Exception 
	{
		logger.debug("request class: " + getRequestClass().getName() + ", response class: " + getResponseClass().getName());

		if (getResponseClass() != IBody.class)
			this.response = (Rsp) getResponseClass().newInstance();
		else
			this.response = null;
	}

}
