package com.zengshi.paas.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.MarkerFactory;

/**
 * 
 * Description: 对 slf4j的再次封装，主要是输出 threadId的信息<br>
 * Date:2015-8-10下午2:48:24  <br>
 * 
 * @version  
 * @since JDK 1.6
 */
public class LogUtil {
    
    private static final String LOG_THREAD_HEADER = "[THREAD_ID:";
    private static final String LOG_THREAD_TAIL = "];";

    private static final String BASE_CLASS_NAME = LogUtil.class.getName();

    private static final String BASE_MARKER_NAME = "logutil";
    
    private static String buildMsg(String msg){
        return LOG_THREAD_HEADER + ThreadId.getThreadId() + LOG_THREAD_TAIL + msg;
    }

    /**
     * add by yugn
     * 记录日志发生的节点信息；
     */
    private static void buildMDC(){
        MDC.clear();
        StackTraceElement element = fetchElement();
        MDC.put("class",element.getClassName());
        MDC.put("file", element.getFileName());
        MDC.put("method",element.getMethodName());
        MDC.put("line",String.valueOf(element.getLineNumber()));
    }

    /**
     * 获取异常描述的节点；
     * @return
     */
    private static StackTraceElement fetchElement(){
        StackTraceElement[] elements = (new Throwable()).getStackTrace();
        if(elements == null || elements.length<2){
            return null;
        }
        //从第一条开始循环，获取第一条，类名不是这个LogUtil类名的Element，就是当时的节点；
        for(int i=0;i<elements.length;i++){
            StackTraceElement element = elements[i];
            if(BASE_CLASS_NAME.equalsIgnoreCase(element.getClassName())){
                continue;
            } else {
                return element;
            }
        }
        //如果一直木有，那么就直接返回最后一条；
        return elements[elements.length-1];
    }
    /**
     * 
     * debug: 输出 debug 信息<br/> 
     * 
     * @param module
     * @param msg 
     * @since JDK 1.6
     */
    public static void debug(String module,String msg){
        debug(module,msg,null);
    }
    
    public static void debug(String module,String msg,Throwable t){
        Logger logger = LoggerFactory.getLogger(module);
        if(logger.isDebugEnabled()){
            buildMDC();
            logger.debug(MarkerFactory.getMarker(BASE_MARKER_NAME),buildMsg(msg),t);
        }
    }
    
    /**
     * 
     * error:error信息打印<br/> 
     * @param module
     * @param msg 
     * @since JDK 1.6
     */
    public static void error(String module,String msg){
        error(module,msg,null);
    }
    
    public static void error(String module,String msg,Throwable t){
        Logger logger = LoggerFactory.getLogger(module);
        if(logger.isErrorEnabled()){
            buildMDC();
            //logger.error(buildMsg(msg),t);
            logger.error(MarkerFactory.getMarker(BASE_MARKER_NAME),buildMsg(msg),t);
        }
    }
    
    /**
     * 
     * info:info信息打印<br/> 
     * @param module
     * @param msg 
     * @since JDK 1.6
     */
    public static void info(String module,String msg){
        info(module,msg,null);
    }
    
    public static void info(String module,String msg,Throwable t){
        Logger logger = LoggerFactory.getLogger(module);
        if(logger.isInfoEnabled()){
            buildMDC();
            //logger.info(buildMsg(msg),t);
            logger.info(MarkerFactory.getMarker(BASE_MARKER_NAME),buildMsg(msg),t);
        }
    }
    
    /**
     * 
     * warn信息打印<br/> 
     * @param module
     * @param msg 
     * @since JDK 1.6
     */
    public static void warn(String module,String msg){
        warn(module,msg,null);
    }
    
    public static void warn(String module,String msg,Throwable t){
        Logger logger = LoggerFactory.getLogger(module);
        if(logger.isWarnEnabled()){
            buildMDC();
            //logger.warn(buildMsg(msg),t);
            logger.warn(MarkerFactory.getMarker(BASE_MARKER_NAME),buildMsg(msg),t);
        }
    }
    

}

