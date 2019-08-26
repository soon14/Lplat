/** 
 * Date:2015年8月19日上午10:37:42 
 * 
*/
package com.zengshi.paas.utils;

import com.zengshi.paas.message.MessageConsumer;
import com.zengshi.paas.message.MessageListener;
import com.zengshi.paas.message.MessageSender;

/** 
 * Description: <br>
 * Date:2015年8月19日上午10:37:42  <br>
 * 
 * @since JDK 1.6 
 * @see       
 */
public class KafkaMessageUtil{
    
    private static MessageSender sender;
    private static MessageConsumer consumer;
    
    static{
        sender=PaasContextHolder.getContext().getBean("messageSender",MessageSender.class);
        consumer=PaasContextHolder.getContext().getBean("messageConsumer",MessageConsumer.class);
    }

    public void setSender(MessageSender sender){
        
        KafkaMessageUtil.sender=sender;
    }
    
    public void setConsumer(MessageConsumer consumer){
        
        KafkaMessageUtil.consumer=consumer;
    }
    /**
     * 
     * send:发送消息. <br/> 
     * 
     * @param message 消息体
     * @param topic 主题
     * @since JDK 1.6
     */
    public static void send(Object message,String topic){
        
        sender.sendMessage(message, topic);
    }
    /**
     * 
     * consumer:消费消息. <br/> 
     * 
     * @param topic 主题
     * @param listener 监听类
     * @since JDK 1.6
     */
    public static void consumer(String topic,MessageListener listener){
        
        consumer.addConsumeMessage(topic, listener);
    }
}

