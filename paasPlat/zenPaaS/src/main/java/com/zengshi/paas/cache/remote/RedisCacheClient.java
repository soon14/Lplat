package com.zengshi.paas.cache.remote;

import com.zengshi.paas.utils.SerializeUtil;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import redis.clients.jedis.*;

import java.util.*;

/**
 * Redis缓存访问类
 * 
 *
 */
public class RedisCacheClient {
	private static final Logger log = Logger.getLogger(RedisCacheClient.class);

	private static final String HOST_KEY = "host";
	private static final String PORT_KEY = "port";
	private static final String TIMEOUT_KEY = "timeOut";
	private static final String MAXACTIVE_KEY = "maxActive";
	private static final String MAXIDLE_KEY = "maxIdle";
	private static final String MAXWAIT_KEY = "maxWait";
	private static final String TESTONBORROW_KEY = "testOnBorrow";
	private static final String TESTONRETURN_KEY = "testOnReturn";
	private static final String TIMEBETWEENEVICTIONRUNSMILLIS_KEY="timeBetweenEvictionRunsMillis";
    private static final String NUMTESTSPEREVICTIONRUN_KEY="numTestsPerEvictionRun";
    private static final String MINEVICTABLEIDLETIMEMILLIS_KEY="minEvictableIdleTimeMillis";
    private static final String TESTWHILEIDLE_KEY="testWhileIdle";
    private static final String SOFTMINEVICTABLEIDLETIMEMILLIS_KEY="softMinEvictableIdleTimeMillis";
	private static final String PASSWORD_KEY="password";
	

	private JedisPool pool;
	private JedisPoolConfig config;

	public RedisCacheClient(String parameter) {
		try {
			JSONObject json = JSONObject.fromObject(parameter);
			if (json != null) {
				config = new JedisPoolConfig();
				config.setMaxActive(json.getInt(MAXACTIVE_KEY));
//				config.setMaxTotal(json.getInt(MAXACTIVE_KEY));
				config.setMaxIdle(json.getInt(MAXIDLE_KEY));
				config.setMaxWait(json.getLong(MAXWAIT_KEY));
//				config.setMaxWaitMillis(json.getLong(MAXWAIT_KEY));
				config.setTestOnBorrow(json.getBoolean(TESTONBORROW_KEY));
				config.setTestOnReturn(json.getBoolean(TESTONRETURN_KEY));
				config.setTimeBetweenEvictionRunsMillis(json.getLong(TIMEBETWEENEVICTIONRUNSMILLIS_KEY));
				config.setNumTestsPerEvictionRun(json.getInt(NUMTESTSPEREVICTIONRUN_KEY));
				config.setMinEvictableIdleTimeMillis(json.getLong(MINEVICTABLEIDLETIMEMILLIS_KEY));
				config.setTestWhileIdle(json.getBoolean(TESTWHILEIDLE_KEY));
				config.setSoftMinEvictableIdleTimeMillis(json.getLong(SOFTMINEVICTABLEIDLETIMEMILLIS_KEY));
				if(json.containsKey(PASSWORD_KEY)){
					pool = new JedisPool(config, json.getString(HOST_KEY),
							json.getInt(PORT_KEY), json.getInt(TIMEOUT_KEY),json.getString(PASSWORD_KEY));
				}else{
					pool = new JedisPool(config, json.getString(HOST_KEY),
							json.getInt(PORT_KEY), json.getInt(TIMEOUT_KEY));
				}

			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public void addItemToList(int dbIndex, String key, Object object) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.connect();
			jedis.select(dbIndex);
			jedis.lpush(key.getBytes(), SerializeUtil.serialize(object));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getItemFromList(int dbIndex, String key) {
		Jedis jedis = null;
		List<byte[]> ss = null;
		List data = new ArrayList();
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			long len = jedis.llen(key);
			if (len == 0)
				return null;
			ss = jedis.lrange(key.getBytes(), 0, (int) len - 1);
			for (int i = 0; i < len; i++) {
				data.add(SerializeUtil.deserialize(ss.get(i)));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
		return data;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List keys(int dbIndex, String keyPattern) {
		Jedis jedis = null;
		Set<byte[]> ss = null;
		List data = new ArrayList();
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			ss = jedis.keys(keyPattern.getBytes());
			for (Iterator<byte[]> iterator = ss.iterator(); iterator.hasNext();) {
				data.add(new String(iterator.next()));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
		return data;

	}

	public void addItem(int dbIndex, String key, Object object) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			jedis.set(key.getBytes(), SerializeUtil.serialize(object));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}

	}

	/**
	 * 节点不存在的时候，写入，
	 * @auth yugn
	 * @param dbIndex
	 * @param key
	 * @param object
	 * @return
	 */
	public boolean addItemNotExists(int dbIndex, String key , Object object){
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			long result = jedis.setnx(key.getBytes(), SerializeUtil.serialize(object));
			//返回值 1，表示设置成功，否则表示设置失败；
			if(result>0){
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
			return false;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}

	public String flushDB(int dbIndex) {
		Jedis jedis = null;
		String result = "";
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			result = jedis.flushDB();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
		return result;
	}

	public void auth(String password){
		Jedis jedis=pool.getResource();
		jedis.auth(password);
	}
	public boolean exists(int dbIndex, String key) {
		Jedis jedis = null;
		boolean result = false;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			result = jedis.exists(key.getBytes());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
		return result;
	}

	public void addItem(int dbIndex, String key, Object object, int seconds) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			jedis.setex(key.getBytes(), seconds,
					SerializeUtil.serialize(object));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public void addItemInt(int dbIndex,String key,Long object){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.select(dbIndex);
            jedis.set(key, String.valueOf(object));
        } catch (Exception e) {
            log.error("exception:" + e);
            pool.returnBrokenResource(jedis);
        } finally {
            if (jedis != null)
                pool.returnResource(jedis);
        }
    }

	/**
	 *设置某个时间点过期
	 * @param key
	 * @param unixTime 1970后毫秒
	 */
	public void expireAt(int dbIndex,String key,long unixTime){
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			jedis.expireAt(key,unixTime);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}

	public Object getItem(int dbIndex, String key) {
		Jedis jedis = null;
		byte[] data = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			data = jedis.get(key.getBytes());
			return SerializeUtil.deserialize(data);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
			return null;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}

	}

	public void delItem(int dbIndex, String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			jedis.del(key.getBytes());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}

	}
	
	public void hsetItem(int dbIndex, String key, String field, Object object) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			jedis.hset(key.getBytes(), field.getBytes(),
					SerializeUtil.serialize(object));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}

	}
	
	public Object hgetItem(int dbIndex, String key, String field) {
		Jedis jedis = null;
		byte[] data = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			data = jedis.hget(key.getBytes(), field.getBytes());
			return SerializeUtil.deserialize(data);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
			return null;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}

	}
	
	public void hdelItem(int dbIndex, String key, String field) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			jedis.hdel(key.getBytes(), field.getBytes());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}

	}

	public Set<String> hkeys(int dbIndex, String key) {
		Jedis jedis = null;
		Set<String> data = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			data = jedis.hkeys(key);
			return data;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
			return null;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}

	}

	public void expire(int dbIndex, String key, int seconds) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			jedis.expire(key.getBytes(), seconds);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}

	public long getIncrement(int dbIndex, String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			return jedis.incr(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
			return 0L;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}

	}

	public long getDecrement(int dbIndex, String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			return jedis.decr(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
			return 0L;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}

	}

	public void setHashMap(int dbIndex, String key, HashMap<String, String> map) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			if (map != null && !map.isEmpty()) {
				for (Map.Entry<String, String> entry : map.entrySet()) {
					jedis.hset(key, entry.getKey(), entry.getValue());
				}

			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}

	}

	public Map<String, String> getHashMap(int dbIndex, String key) {
		Map<String, String> map = new HashMap<String, String>();
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			map = jedis.hgetAll(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
		return map;

	}

	public void addSet(int dbIndex, String key, Set<String> set) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			if (set != null && !set.isEmpty()) {
				for (String value : set) {
					jedis.sadd(key, value);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}

	public Set<String> getSet(int dbIndex, String key) {
		Set<String> sets = new HashSet<String>();
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			sets = jedis.smembers(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}

		return sets;
	}
	
	/**
	 * 
	 * saddItem: 将对象加入 set 中；会对 object做 serialize处理；<br/>
	 * 
	 * @param dbIndex
	 * @param key
	 * @param object 
	 * @since JDK 1.6
	 */
	public void saddItem(int dbIndex, String key ,Object object){
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			jedis.sadd(key.getBytes(), SerializeUtil.serialize(object));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}

	/**
	 * saddItem: 将member-->String类型；加入 set 中；<br/>
	 * @param dbIndex
	 * @param key
	 * @param member
	 */
	public void saddStringMember(int dbIndex, String key, String member){
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			jedis.sadd(key,member);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}

	/**
	 * 判断 value 是否是 key 集合的一个成员；
	 * @param dbIndex
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean sisMember(int dbIndex, String key, String value){
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			return jedis.sismember(key,value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
			return false;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}

	/**
	 * 对需要的集合进行合并后输出，集合的并集运算；
	 * @param dbIndex
	 * @param keys
	 * @return
	 */
	public Set<String> sUnion(int dbIndex, String... keys){
		Set<String> sets = new TreeSet<String>();
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			sets = jedis.sunion(keys);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
		return sets;
	}

	/**
	 * 删除 key 集合对应的 member 节点；
	 * @param dbIndex
	 * @param key
	 * @param member
	 */
	public void sRemMember(int dbIndex, String key, String member){
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(dbIndex);
			jedis.srem(key,member);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			pool.returnBrokenResource(jedis);
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public void addItemFile(String key, byte[] file) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key.getBytes(), file);
        } catch (Exception e) {
            log.error("",e);
            pool.returnBrokenResource(jedis);
        } finally {
            if (jedis != null)
                pool.returnResource(jedis);
        }

    }
	
	/**
	 * 
	 * zaddItem: 加入zset 节点 <br/> 
	 * 
	 * @param dbIndex
	 * @param key
	 * @param item
	 * @param score 
	 * @since JDK 1.6
	 */
	public void zaddItem(int dbIndex, String key ,String item, double score){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.select(dbIndex);
            jedis.zadd(key, score, item);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            pool.returnBrokenResource(jedis);
        } finally {
            if (jedis != null)
                pool.returnResource(jedis);
        }
    }

    /**
     *
     * zdelItem: 删除zset 节点 <br/>
     *
     * @param dbIndex
     * @param key
     * @param item
     * @since JDK 1.6
     */
    public Long zdelItem(int dbIndex, String key ,String item){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.select(dbIndex);
            return jedis.zrem(key, item);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            pool.returnBrokenResource(jedis);
        } finally {
            if (jedis != null)
                pool.returnResource(jedis);
        }
        return null;
    }

    /**
     *
     * zrank: 返回 zset 节点 <br/>
     *
     * @param dbIndex
     * @param key
     * @param item
     * @since JDK 1.6
     */
    public Long zrank(int dbIndex, String key ,String item){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.select(dbIndex);
            return jedis.zrank(key,item);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            pool.returnBrokenResource(jedis);
        } finally {
            if (jedis != null)
                pool.returnResource(jedis);
        }
        return null;
    }

	/**
	 * 
	 * zgetItem: 根据 key 获取 zset<br/>   zgetScore
	 * 
	 * @param dbIndex
	 * @param key
	 * @return 
	 * @since JDK 1.6
	 */
	public Set<String> zgetItems(int dbIndex, String key){
	    Set<String> sets = new TreeSet<String>();
	    Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.select(dbIndex);
            sets = jedis.zrange(key, 0, -1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            pool.returnBrokenResource(jedis);
        } finally {
            if (jedis != null)
                pool.returnResource(jedis);
        }
        return sets;
	}

    /**
     *
     * zgetScore: 根据 key,item 获取 顺序值 <br/>
     *
     * @param dbIndex
     * @param key
     * @return
     * @since JDK 1.6
     */
    public Double zgetScore(int dbIndex, String key,String item) {
        Double score = null;
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.select(dbIndex);
            score = jedis.zscore(key, item);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            pool.returnBrokenResource(jedis);
        } finally {
            if (jedis != null)
                pool.returnResource(jedis);
        }
        return score;
    }

    /**
     *
     * zgetScore: 根据 key,item 修改 顺序值 <br/>
     *
     * @param dbIndex
     * @param key
     * @return
     * @since JDK 1.6
     */
    public Double zincrby(int dbIndex, String key,String item,Double score) {
        Double res = null;
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.select(dbIndex);
            res = jedis.zincrby(key, score, item);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            pool.returnBrokenResource(jedis);
        } finally {
            if (jedis != null)
                pool.returnResource(jedis);
        }
        return res;
    }

	/**
	 * 推送消息
	 * @param channel 管道
	 * @param message 消息
	 * @return
	 */
	public Long publish(String channel,String message){
		Jedis jedis=null;
		try {
			jedis=pool.getResource();
			return jedis.publish(channel,message);
		} finally {
			if(null!=jedis){
				pool.returnBrokenResource(jedis);
			}
		}
	}

	/**
	 * 推送消息
	 * @param channel
	 * @param message
	 * @return
	 */
	public Long publish(byte[] channel,byte[] message){
		Jedis jedis=null;
		try {
			jedis=pool.getResource();
			return jedis.publish(channel,message);
		} finally {
			if(null!=jedis){
				pool.returnBrokenResource(jedis);
			}
		}
	}

	/**
	 * 订阅消息
	 * @param jedisPubSub
	 * @param channels
	 */
	public void subscribe(JedisPubSub jedisPubSub,String... channels){
		Jedis jedis=null;
		try {
			jedis=pool.getResource();
			jedis.subscribe(jedisPubSub,channels);
		} finally {
			if(null!=jedis){
				pool.returnBrokenResource(jedis);
			}
		}
	}

	/**
	 * 订阅消息
	 * @param jedisPubSub
	 * @param channels
	 */
	public void subscribe(BinaryJedisPubSub jedisPubSub,byte[]... channels){
		Jedis jedis=null;
		try {
			jedis=pool.getResource();
			jedis.subscribe(jedisPubSub,channels);
		} finally {
			if(null!=jedis){
				pool.returnBrokenResource(jedis);
			}
		}
	}

	/**
	 * 订阅消息
	 * @param jedisPubSub
	 * @param patterns 匹配
	 */
	public void psubscribe(BinaryJedisPubSub jedisPubSub,byte[]... patterns){
		Jedis jedis=null;
		try {
			jedis=pool.getResource();
			jedis.psubscribe(jedisPubSub, patterns);
		} finally {
			if(null!=jedis){
				pool.returnBrokenResource(jedis);
			}
		}
	}

	/**
	 * 订阅消息
	 * @param jedisPubSub
	 * @param patterns 匹配
	 */
	public void psubscribe(JedisPubSub jedisPubSub,String... patterns){
		Jedis jedis=null;
		try {
			jedis=pool.getResource();
			jedis.psubscribe(jedisPubSub, patterns);
		} finally {
			if(null!=jedis){
				pool.returnBrokenResource(jedis);
			}
		}
	}

}
