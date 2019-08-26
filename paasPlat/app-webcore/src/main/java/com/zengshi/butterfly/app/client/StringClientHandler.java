package com.zengshi.butterfly.app.client;

import com.zengshi.butterfly.app.MappContext;
import com.zengshi.butterfly.app.annotation.Crypt;
import com.zengshi.butterfly.app.interfaces.IClientHandler;
import com.zengshi.butterfly.app.model.IAppDatapackage;
import com.zengshi.butterfly.core.security.encrypt.EncrpytEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;


/**
 * 
 * 请求和返回参数均为String的处理器，可实现为JSON或者XML
 * @param <T>
 */
public abstract class StringClientHandler<T extends IAppDatapackage>
{
	
	public Logger logger = LoggerFactory.getLogger(StringClientHandler.class);
	
	@Autowired
	private IClientHandler<T> clientHandler;
	
	protected Class<? extends IAppDatapackage> entityClass;

	/**
	 * 用于Dao层子类使用的构造函数.
	 * 通过子类的泛型定义取得对象类型Class.
	 * eg.
	 * public class UserDao extends SimpleHibernateDao<User, Long>
	 */
	public StringClientHandler() {
		
		
	}
	
	@Crypt(algorithm=EncrpytEnum.DES, desKey="APP_DESKEY", base64Wrapped=true, gzip=true)
	public String doHandle(String request,Map<String, Object> attributes) throws Exception
	{
		MappContext.setRequestString(request);
		
		T responseObject= clientHandler.doHandlePackage(preProcess(request), attributes);
		
		return buildResponse(responseObject);
	}
	
	protected abstract T preProcess(String request) throws Exception ;
	
	protected abstract String buildResponse(T response) throws Exception ;

	public void setHandler(IClientHandler<T> clientHandler) {
		this.clientHandler = clientHandler;
	}
	
	private Class<? extends IAppDatapackage> getEntityClass()
	{
		Type genType = getClass().getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			logger.warn(getClass().getSimpleName() + "'s superclass not ParameterizedType");
			entityClass = IAppDatapackage.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (0 >= params.length || 0 < 0) {
			logger.warn("Index: " + 0 + ", Size of " + getClass().getSimpleName() + "'s Parameterized Type: "
					+ params.length);
			entityClass = IAppDatapackage.class;
		}
		if (!(params[0] instanceof Class)) {
			logger.warn(getClass().getSimpleName() + " not set the actual class on superclass generic parameter");
			entityClass =  IAppDatapackage.class;
		}

		entityClass = (Class<? extends IAppDatapackage>) params[0];
		
		return entityClass;
	}
}
