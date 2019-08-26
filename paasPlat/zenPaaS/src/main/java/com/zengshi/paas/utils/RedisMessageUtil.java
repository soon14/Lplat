package com.zengshi.paas.utils;

import com.zengshi.paas.cache.remote.RemoteCacheSVC;
import com.zengshi.paas.message.Message;
import com.zengshi.paas.message.MessageListener;
import com.zengshi.paas.message.MessageStatus;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import redis.clients.jedis.BinaryJedisPubSub;

import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;

/**
 */
public class RedisMessageUtil {

    private static final Logger logger=Logger.getLogger(RedisMessageUtil.class);

    private static RemoteCacheSVC cacheSVC;

    static{
        cacheSVC=PaasContextHolder.getContext().getBean("cacheSvc", RemoteCacheSVC.class);
    }

    /**
     * 推送消息
     * @param topic 主题
     * @param message 消息体
     */
    public static void send(String topic,Object message){

        cacheSVC.publish(topic.getBytes(), object2byte(message));
    }

    /**
     * 订阅消息（需要自定义线程运行）
     * @param topic 主题
     * @param listener 监听对象
     */
    public static void consumer(String topic, final MessageListener listener){

        cacheSVC.subscribe(new BinaryJedisPubSub() {
            @Override
            public void onMessage(byte[] channel, byte[] message) {
                Message msg = new Message();
                msg.setMsg(byte2object(message));
                msg.setTopic(new String(channel));
                listener.receiveMessage(msg, new MessageStatus());
            }

            @Override
            public void onPMessage(byte[] pattern, byte[] channel, byte[] message) {

            }

            @Override
            public void onSubscribe(byte[] channel, int subscribedChannels) {
                logger.debug("新增订阅:" + new String(channel) + "  " + subscribedChannels);
            }

            @Override
            public void onUnsubscribe(byte[] channel, int subscribedChannels) {
                logger.debug("取消订阅:" + new String(channel) + "  " + subscribedChannels);
            }

            @Override
            public void onPUnsubscribe(byte[] pattern, int subscribedChannels) {
                logger.debug("取消匹配订阅:" + new String(pattern) + "  " + subscribedChannels);
            }

            @Override
            public void onPSubscribe(byte[] pattern, int subscribedChannels) {
                logger.debug("新增匹配订阅:" + new String(pattern) + "  " + subscribedChannels);
            }
        }, topic.getBytes());
    }

    /**
     * 订阅消息
     * @param topic
     * @param listener
     * @param threadNums
     */
    public static void consumer(final String topic, final MessageListener listener,int threadNums){

//        for(int i=0;i<threadNums;i++){
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println("新增监听   ");
//                    consumer(topic,listener);
//                }
//            }).start();
//        }

        Executors.newFixedThreadPool(threadNums).execute(new Runnable() {
            @Override
            public void run() {
                consumer(topic,listener);
            }
        });
    }


    /**
     * 对象转换成字节数组
     * @param object
     * @return
     */
    public static byte[] object2byte(Object object){
        byte[] bytes = null;
        ByteArrayOutputStream bos=null;
        ObjectOutputStream oos=null;
        try {
            bos=new ByteArrayOutputStream();
            oos=new ObjectOutputStream(bos);
            oos.writeObject(object);

            bytes=bos.toByteArray();
        } catch (IOException e) {
            logger.error("对象转换成字节数组失败。",e);
        }finally {
            if(null!=bos){
                try {
                    bos.close();
                } catch (IOException e) {
                    logger.error("对象转换成字节数组失败。",e);
                }
            }
            if(null!=oos){
                try {
                    oos.close();
                } catch (IOException e) {
                    logger.error("对象转换成字节数组失败。",e);
                }
            }
        }
        return bytes;
    }

    /**
     * 字节数组转换成对象
     * @param bytes
     * @return
     */
    public static Object byte2object(byte[] bytes){
        Object object=null;

        ByteArrayInputStream bis=null;
        ObjectInputStream ois=null;
        try {
            bis=new ByteArrayInputStream(bytes);
            ois=new ObjectInputStream(bis);
            object=ois.readObject();
        } catch (Exception e) {
            logger.error("字节数组转换成对象失败。",e);
        } finally {
            if(null!=ois){
                try {
                    ois.close();
                } catch (IOException e) {
                    logger.error("字节数组转换成对象失败。",e);
                }
            }
            if(null!=bis){
                try {
                    bis.close();
                } catch (IOException e) {
                    logger.error("字节数组转换成对象失败。",e);
                }
            }
        }
        return object;
    }
}
