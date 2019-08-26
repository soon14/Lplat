/** 
 * Date:2015年8月19日上午10:37:42 
 * 
*/
package com.zengshi.paas.utils;

import com.zengshi.paas.message.MessageListener;

/** 
 * Description: <br>
 * Date:2015年8月19日上午10:37:42  <br>
 * 
 * @since JDK 1.6 
 * @see       
 */
public class MessageUtil {

    public static String PROTOCOL_KAFKA="kafka";
    public static String PROTOCOL_REDIS="redis";

    private static String protocol;

    private static int threadNum=3;//redis的订阅处理线程数

    public void setProtocol(String protocol) {

        MessageUtil.protocol = checkProtocol(protocol)?protocol.trim().toLowerCase():MessageUtil.PROTOCOL_KAFKA;
    }

    public void setThreadNum(int threadNum) {
        MessageUtil.threadNum = threadNum;
    }

    /**
     * 消息发送
     * @param message
     * @param topic
     */
    public static void send(Object message,String topic){
        if(PROTOCOL_REDIS.equals(protocol)){
            RedisMessageUtil.send(topic,message);
        }else{
            KafkaMessageUtil.send(message,topic);
        }
    }

    /**
     * 消息订阅
     * @param topic
     * @param listener
     */
    public static void consumer(String topic,MessageListener listener){
        if(PROTOCOL_REDIS.equals(protocol)){
            RedisMessageUtil.consumer(topic,listener,threadNum);
        }else{
            KafkaMessageUtil.consumer(topic,listener);
        }
    }

    public static boolean checkProtocol(String protocol){

        if(PROTOCOL_KAFKA.equalsIgnoreCase(protocol.trim()) || PROTOCOL_REDIS.equalsIgnoreCase(protocol.trim())){
            return true;
        }

        return false;
    }
}

