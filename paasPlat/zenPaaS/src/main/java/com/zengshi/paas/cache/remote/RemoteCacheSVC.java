package com.zengshi.paas.cache.remote;

import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 远程缓存服务接口类
 *
 */
public interface RemoteCacheSVC {
	public void addItemToList(String key, Object object);
	@SuppressWarnings("rawtypes")
	public List getItemFromList(String key);
	public void addItem(String key, Object object);

	/**
	 * 将数据设置到对应的节点 key 上；要保证key不存在的时候，才能设置成功；
	 * 2016.11.19补充；
	 * @Auth yugn;
	 * @param key
	 * @param object
	 * @return key 不存在，设置成功，返回 true ;
	 *   key 存在，设置不成功，返回false;
	 */
	public boolean addItemNotExists(String key , Object object);

	public void addItem(String key, Object object, int seconds);
	public void addItemInt(String key,Long object);
	public String flushDB();
	public boolean exists(String key);
	public void auth(String password);
	@SuppressWarnings("rawtypes")
	public List keys(String keyPattern);
	public Object getItem(String key);
	public void delItem(String key);
	public long getIncrement(String key);
	public long getDecrement(String key);
	public void setHashMap(String key, HashMap<String, String> map);
	public Map<String, String> getHashMap(String key);

	public void hsetItem(String key, String field,Object object);
	public Object hgetItem(String key, String field);
	public void hdelItem(String key, String field);

	public Set<String> hkeys(String key);

    /// 对到期方法的封装

    /**
     * 多久时间失效；
     * @param key
     * @param seconds
     */
    public void expire(String key,int seconds);

    /**
     * 截止到什么时间失效；
     * @param key
     * @param unixTime
     */
    void expireAt(String key,long unixTime);

///// 对Set 数据格式方法的封装；

	/**
	 * saddItem: 将对象加入 key 对应的 set 结构中 ；对应  sadd 方法，
	 * @param key
	 * @param object 
	 */
	public void saddItem(String key, Object object);

	/**
	 * saddStringMember 将String加入到 Set结构中；适合于集合是String的处理；
	 * @param key
	 * @param member
	 */
	public void saddStringMember(String key, String member);

	/**
	 * addSet 将对象加入Set结构，对应 sadd方法；
	 * @param key
	 * @param set
	 */
	public void addSet(String key, Set<String> set);

	/**
	 * getSet 获取 Set结果的所有集合元素；对应 smember方法；
	 * @param key
	 * @return
	 */
	public Set<String> getSet(String key);

	/**
	 * 判断value 是否在  key 的集合内；sismember的命令；
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean isSetMember(String key, String value);

	/**
	 * 获取多个 key 对应的集合的并集； sunion的命令；
	 * @param keys 需要并集的集合列表；
	 * @return
	 */
	public Set<String> sUnion(String... keys);

	/**
	 * 根据key删除集合中 member值；
	 * @param key
	 * @param member
	 */
	public void sRemMember(String key ,String member);

    public void addItemFile(String key, byte[] file);

  ///// 对 SortSet 数据格式方法的封装；

    public void zaddItem(String key, String item, double score);

    public Long zdelItem(String key, String item);
	
	public Set<String> zgetItems(String key);

    public Long zrank(String key, String item);

    public Double zgetScore(String key,String item);

    public Double zincrby(String key,String item,Double score);
    ////启动对 发布、监听方法的封装；
	public Long publish(String channel,String message);

	public Long publish(byte[] channel,byte[] message);

	public void subscribe(JedisPubSub jedisPubSub,String... channels);

	public void subscribe(BinaryJedisPubSub jedisPubSub,byte[]... channels);

	public void psubscribe(BinaryJedisPubSub jedisPubSub,byte[]... patterns);

	public void psubscribe(JedisPubSub jedisPubSub,String... patterns);



}
