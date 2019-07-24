package uyun.show.server.domain.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import uyun.show.server.domain.Constants;

import java.util.HashSet;
import java.util.Set;

public class RedisUtil {

    protected final static Logger logger = LoggerFactory.getLogger(RedisUtil.class.getName());

    private static JedisSentinelPool pool = null;

    public static Jedis jedis = null;

    public static Jedis getJedis() {

        if (pool == null) {
            JedisSentinelPool();
        }

        return pool.getResource();
    }

    private static synchronized void poolInit() {
        if (pool == null) {
            JedisSentinelPool();
        }
    }

    private static JedisPoolConfig getPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(20000);
        config.setMaxIdle(200);
        config.setTestOnBorrow(true);
        return config;
    }

    public static void JedisSentinelPool() {
        try {
            Set<String> sentinels = new HashSet<String>();
            String[] nodes = Constants.Redis_Sentinel_Nodes.split(",");
            if (nodes != null) {
                for (String node : nodes
                ) {
                    sentinels.add(node);//redis.sentinel.nodes
                }
            }

            pool = new JedisSentinelPool(Constants.Redis_Master, sentinels, getPoolConfig(), 2000, Constants.Redis_Password);

        } catch (Exception e) {
            logger.error("JedisSentinelPool Exception : " + e);
        }
    }

    public static String get(String key) {
        key = "show:" + key;
        Jedis jedis = null;
        String value = null;
        try {
            jedis = getJedis();
            value = jedis.get(key);
        } catch (Exception e) {

        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    public static String set(String key, String value) {
        key = "show:" + key;
        if (value == null) {
            return value;
        }
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.set(key, value);
        } catch (Exception e) {

        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public static String rpush(String key, String value) {
        key = "show:" + key;
        if (value == null) {
            return value;
        }
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.rpush(key, value);
        } catch (Exception e) {

        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public static long delete(String key) {
        return getJedis().del(key);
    }

}
