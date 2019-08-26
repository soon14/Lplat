/** 
 * Date:2015年8月4日下午5:17:03 
 * 
*/
package com.zengshi.paas.validator.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.CollectionUtils;

import com.zengshi.paas.utils.LogUtil;

/**
 * Description: bean 验证拦截器<br>
 * Date:2015年8月4日下午5:17:03 <br>
 * 
 * @since JDK 1.6
 * @see
 */
public class ValidationInterceptor {
    
    private ValidatorFactory validatorFactroy;
    
    private boolean interrupt;

    public void doValid(JoinPoint jp) {

        Method md = ((MethodSignature) jp.getSignature()).getMethod();
        Object target = jp.getTarget();
        Method method = null;
        try {
            method = target.getClass().getMethod(jp.getSignature().getName(),
                    md.getParameterTypes());
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
        Annotation[][] annotationses = method.getParameterAnnotations();

        if (null == annotationses) {
            return;
        }
        Object[] args = jp.getArgs();
        if (null == args) {
            return;
        }
        int argSize = args.length;
        int index = 0;
        Validator validator = this.validatorFactroy.getValidator();
        for (Annotation[] annotations : annotationses) {
            for (Annotation annotation : annotations) {
                if (Valid.class.isAssignableFrom(annotation.getClass())) {
                    Object arg = args[index];
                    if (perHandler(arg)) {
                        break;
                    }
                    Set<ConstraintViolation<Object>> set = validator.validate(arg);
                    if (!CollectionUtils.isEmpty(set)) {
                        int size = set.size();
                        Iterator<ConstraintViolation<Object>> iter = set.iterator();
                        StringBuilder sb = new StringBuilder(128);
                        for (int i = 1; iter.hasNext(); i++) {
                             boolean flag=this.interrupt;
                             if(i==size && (index+1)==argSize){
                                 flag=true;
                             }
                             ConstraintViolation<Object> obj=iter.next();
                             
                             StringBuffer sf=new StringBuffer(64);
                             sf.append("object: ")
                                 .append(obj.getLeafBean().getClass().getName())
                                 .append(" field: ")
                                 .append(obj.getPropertyPath().iterator().next().getName())
                                 .append(" value: ")
                                 .append(obj.getInvalidValue())
                                 .append("    ")
                                 .append(obj.getMessage());
                             LogUtil.info("filed validator", sf.toString());
                             sb.append(obj.getMessage()+"\n");
                             if(flag){
                                 throw new RuntimeException(sb.toString());
                             }
                            // MessageContext.throwValidMessage(iter.next().getMessage(),flag);
                        }
                    }
                    break;
                }
            }
            index++;
        }

    }

    private boolean perHandler(Object arg) {
        if (null == arg) {
            return true;
        }
        Class<?> clazz = arg.getClass();
        if (clazz.isPrimitive()) {
            return true;
        }
        return false;
    }

    public void setValidatorFactroy(ValidatorFactory validatorFactroy) {

        this.validatorFactroy = validatorFactroy;
    }

    public void setInterrupt(boolean interrupt) {
        this.interrupt = interrupt;
    }
    
}
