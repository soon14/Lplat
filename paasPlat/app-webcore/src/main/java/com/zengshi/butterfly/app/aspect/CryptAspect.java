package com.zengshi.butterfly.app.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.zengshi.butterfly.app.annotation.Crypt;
import com.zengshi.butterfly.core.annotation.Config;
import com.zengshi.butterfly.core.util.DESUtils;
import com.zengshi.butterfly.core.util.GZipUtils;

@Aspect
@Component
public class CryptAspect {
	
	Logger logger = LoggerFactory.getLogger(CryptAspect.class);
	
	@Config(value="action.cryptPermission") 
	private boolean cryptPermission;
	
	/**
	 * 拦截处理actionHandle方法，在此方法前执行已经定义的filter
	 * @param context
	 * @throws Throwable 
	 */
	@Around("within(com.zengshi.butterfly.app..*) && @annotation(crypt)")
	public Object AroundCrypt(ProceedingJoinPoint point,Crypt crypt) throws Throwable 
	{
		logger.debug("进入拦截器 AroundCrypt.AroundCrypt。");
		
		MethodSignature joinPointObject = (MethodSignature) point.getSignature();  
        Method method = joinPointObject.getMethod();    
        Crypt crypt_annotation = method.getAnnotation(Crypt.class);
		
		Object[] args = point.getArgs();
		
		if(cryptPermission == false || crypt_annotation == null)
			return point.proceed(args);
		
		logger.debug("注释 @Crypt( crypt_annotation="+crypt_annotation.algorithm()+",desKey="+crypt_annotation.desKey()+",base64Wrapped="+crypt_annotation.base64Wrapped()+" )");
		/**
		 * 当拦截的方法第一个参数对象为String类型时，将参数进行解密操作
		 */
		if(args != null && args.length != 0 && args[0].getClass() == String.class)
		{
			logger.debug("对参数进行解密操作。");
			
			byte[] arg0_byte = ((String)args[0]).getBytes("utf-8");
			
			if(crypt_annotation.base64Wrapped())
				arg0_byte = DESUtils.decryptBASE64((String)args[0]);
			
			if(crypt_annotation.gzip())
				arg0_byte = GZipUtils.decompress(arg0_byte);
			
			args[0] = DESUtils.decrypt(new String(arg0_byte), crypt_annotation.desKey());
		
		}
		
		Object result = point.proceed(args);
		
		/**
		 * 如果返回参数为String 则加密返回参数
		 */
		if(method.getReturnType() == String.class)
		{
			result = DESUtils.encrypt((String)result, crypt_annotation.desKey());
			
			byte[] ret_byte = ((String)result).getBytes("utf-8");
			
			if(crypt_annotation.gzip())
				ret_byte = GZipUtils.compress(ret_byte);
			
			if(crypt_annotation.base64Wrapped())
				ret_byte = DESUtils.encryptBASE64(ret_byte).getBytes("utf-8");
			
			return new String(ret_byte,"utf-8");
		}
		
		return result;
	}
	
}
