/**
 * 
 */
package com.zengshi.butterfly.app.client;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.zengshi.ecp.base.util.ApplicationContextUtil;
import com.zengshi.butterfly.app.MappContext;
import com.zengshi.butterfly.app.exception.MappException;
import com.zengshi.butterfly.app.exception.MappExceptionCode;
import com.zengshi.butterfly.app.interfaces.IClientHandler;
import com.zengshi.butterfly.app.interfaces.IHandler;
import com.zengshi.butterfly.app.model.IAppDatapackage;
import com.zengshi.butterfly.core.exception.SystemException;

/**
 * 
 * @modify mler 重构此Class，将filters 独立出来
 */
@Service
public class ClientHandler<T extends IAppDatapackage> implements IClientHandler<T> {

	private static final Logger logger = Logger.getLogger(ClientHandler.class);

	/*@Autowired
	protected MyApplicationContext factory;
*/
	protected void initContext(T requestObject) throws Exception {
		MappContext.initContext(null, requestObject, null);
	}
	
	/** 获取泛型 Class **/
	/*private Class<?> getPackageClass() throws Exception
	{
		ParameterizedType type = (ParameterizedType)getClass().getGenericInterfaces()[0];
		logger.debug(type.getActualTypeArguments()[0].toString());
		Class.forName(type.getActualTypeArguments()[0].toString()).newInstance();
		return (Class<?>) ((ParameterizedType) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
	}*/

	/**
	 * 初始化mapp上下文
	 * 
	 * @param request
	 *            ，requestObject
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("unchecked")
	protected void initContext(T requestObject, Map<String, Object> attributes) throws Exception {
		try {
			/** 每次新生成一个上下文 **/
			MappContext.initContext(requestObject,attributes);
			
			T responseObject = (T) requestObject.getClass().newInstance();
			responseObject.setHeader(requestObject.getHeader());
			MappContext.setResponse(responseObject);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new MappException(MappExceptionCode.UNEXPECT_ERROR, e);
		}
	}

	/**
	 * 传入请求对象，并调用方法获得结果
	 * 
	 * @param request
	 * @return
	 * @throws SystemException
	 * @throws MappException
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T doHandlePackage(T requestObject, Map<String, Object> attributes)
			throws Exception {

		initContext(requestObject, attributes);

		doHandle();

		return (T) MappContext.getResponse();
	}

	@Override
	/**
	 * 根据上下文中的request对象，调用接口，获取response对象并保存在上下文中
	 * 
	 * @param context
	 * @return
	 * @throws MappException
	 */
	public void doHandle() throws Exception 
	{
		IHandler actionHandler = findAction();
		actionHandler.doHandle();
	}

	/** 获取与bizcode对应的Action处理类 **/
	private IHandler findAction() {
	    IHandler action = ApplicationContextUtil.getBean(MappContext.getRequest().getHeader().getBizCode(), IHandler.class);
		return action;
	}

	/*public void setFactory(MyApplicationContext factory) {
		this.factory = factory;
	}*/

}
