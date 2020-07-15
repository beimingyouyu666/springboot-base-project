package com.yangk.baseproject.common.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
public class JedisClusterUtil {
    private static final Logger log = LoggerFactory.getLogger(JedisClusterUtil.class);

    private static final int DEFAULT_SINGLE_EXPIRE_TIME = 5;

    @Autowired
    private JedisCluster jedisCluster;

    public static final int ONE_DAY = 86400;

    public static final int ONE_WEEK = 604800;

    private static final String LOCK_SUCCESS = "OK";

    private static final String SET_IF_NOT_EXIST = "NX";

    private static final String SECOND_EXPIRE_TIME = "EX";

    public void remove(String... keys) {
        for (String key : keys)
            remove(key);
    }

    public boolean exists(String key) {
        return this.jedisCluster.exists(key).booleanValue();
    }

    public boolean cacheIfNotExists(String key, String value, Integer expireTime) {
        if (!exists(key))
            return set(key, value, expireTime.intValue());
        return true;
    }

    public static byte[] objectToByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
            bytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            log.error("objectToByteArray failed, " + e);
        } finally {
            if (objectOutputStream != null)
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    log.error("close objectOutputStream failed, " + e);
                }
            if (byteArrayOutputStream != null)
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    log.error("close byteArrayOutputStream failed, " + e);
                }
        }
        return bytes;
    }

    public Long llen(String key) {
        return this.jedisCluster.llen(key);
    }

    public List<String> brpop(int timeout, String key) {
        return this.jedisCluster.brpop(timeout, key);
    }

    public Long lpush(String key, String... string) {
        return this.jedisCluster.lpush(key, string);
    }

    public boolean setnx(String key, int expireTime) {
        String result = this.jedisCluster.set(key, "", "NX", "EX", expireTime);
        return "OK".equals(result);
    }

    public Long hset(String key, String field, String value) {
        return this.jedisCluster.hset(key, field, value);
    }

    public String hget(String key, String field) {
        return this.jedisCluster.hget(key, field);
    }

    public Long hdel(String key, String... field) {
        return this.jedisCluster.hdel(key, field);
    }

    public Long zadd(String key, double score, String member) {
        return this.jedisCluster.zadd(key, score, member);
    }

    public Long sadd(String key, String... members) {
        return this.jedisCluster.sadd(key, members);
    }

    public Set<String> spop(String key, long count) {
        return this.jedisCluster.spop(key, count);
    }

    public Long scard(String key) {
        return this.jedisCluster.scard(key);
    }

    public void removePattern(String pattern) {
        this.jedisCluster.del(pattern);
        log.debug("del key >" + pattern);
    }

    public void remove(String key) {
        if (exists(key)) {
            this.jedisCluster.del(key);
            log.debug("del key >" + key);
        } else {
            log.debug("del key >" + key + "not exist");
        }
    }

    @Deprecated
    public TreeSet<String> keys(String pattern) {
        log.debug("Start getting keys...");
        TreeSet<String> keys = new TreeSet<>();
        Map<String, JedisPool> clusterNodes = this.jedisCluster.getClusterNodes();
        for (String k : clusterNodes.keySet()) {
            log.debug("Getting keys from: {}", k);
            JedisPool jp = clusterNodes.get(k);
            Jedis connection = jp.getResource();
            try {
                keys.addAll(connection.keys(pattern));
            } catch (Exception e) {
                log.error("Getting keys error: {}", e);
            } finally {
                log.debug("Connection closed.");
                connection.close();
            }
        }
        log.debug("Keys gotten!");
        return keys;
    }

    public boolean set(String key, String value) {
        boolean result = false;
        try {
            this.jedisCluster.set(key, value);
            result = true;
        } catch (Exception e) {
            log.error("jedis error:{}", e);
        }
        return result;
    }

    public boolean set(String key, String value, int expireTime) {
        boolean result = false;
        try {
            this.jedisCluster.setex(key, expireTime, value);
            result = true;
        } catch (Exception e) {
            log.error("jedis error:{}", e);
        }
        return result;
    }

    public boolean setIdempotentCache(String key, String requestUUID, int expireTime) {
        String result = this.jedisCluster.set(key, requestUUID, "NX", "EX", expireTime);
        if ("OK".equals(result))
            return true;
        return false;
    }

    public boolean tryLock(String key, String requestUUID, Long tryLockTime, Integer expireTime) {
        try {
            return tryLockUntilTryLockTimeOut(key, requestUUID, tryLockTime.longValue(), TimeUnit.SECONDS, expireTime.intValue());
        } catch (Exception e) {
            log.warn(":{}", e);
            return false;
        }
    }

    @Deprecated
    public boolean tryLockUntilTrue(String lockKey, String requestId) throws InterruptedException {
        while (true) {
            String result = this.jedisCluster.set(lockKey, requestId, "NX", "EX", 5L);
            if ("OK".equals(result))
                return true;
            Thread.sleep(100L);
        }
    }

    private boolean tryLockUntilTryLockTimeOut(String key, String requestUUID, long tryLockTime, TimeUnit unit, int expireTime) throws InterruptedException {
        return getLock(key, requestUUID, tryLockTime, unit, expireTime).booleanValue();
    }

    private Boolean getLock(String key, String requestUUID, long tryLockTime, TimeUnit unit, int expireTime) throws InterruptedException {
        long begin = System.nanoTime();
        do {
            String result = this.jedisCluster.set(key, requestUUID, "NX", "EX", expireTime);
            if ("OK".equals(result)) {
                return Boolean.valueOf(true);
            }
            if (tryLockTime == 0L) {
                break;
            }
            Thread.sleep(100L);
        } while (System.nanoTime() - begin < unit.toNanos(tryLockTime));
        return Boolean.valueOf(false);
    }

    public boolean unLock(String key, String requestUUID) {
        try {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object result = this.jedisCluster.eval(script, Collections.singletonList(key), Collections.singletonList(requestUUID));
            return result.equals(Long.valueOf(1L));
        } catch (Exception e) {
            log.error("redis {}", e);
            return false;
        }
    }

    public static Object byteArrayToObject(byte[] bytes) {
        Object obj = null;
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            obj = objectInputStream.readObject();
        } catch (Exception e) {
            log.error("byteArrayToObject failed, " + e);
        } finally {
            if (byteArrayInputStream != null)
                try {
                    byteArrayInputStream.close();
                } catch (IOException e) {
                    log.error("close byteArrayInputStream failed, " + e);
                }
            if (objectInputStream != null)
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    log.error("close objectInputStream failed, " + e);
                }
        }
        return obj;
    }

    public String get(String key) {
        return this.jedisCluster.get(key);
    }

    public boolean tryLockNotWait(String key, String requestUUID, String unit, int expireTime) {
        String result = this.jedisCluster.set(key, requestUUID, "NX", unit, expireTime);
        return "OK".equals(result);
    }

    @Deprecated
    public Object getObject(String key) {
        byte[] keyByte = objectToByteArray(key);
        return this.jedisCluster.get(keyByte);
    }

    public <T> String setT(String key, T t) {
        return this.jedisCluster.set(key, JSONObject.toJSONString(t));
    }

    public <T> String setexT(String key, int seconds, T t) {
        return this.jedisCluster.setex(key, seconds, JSONObject.toJSONString(t));
    }

    public Long expire(String key, int seconds) {
        return this.jedisCluster.expire(key, seconds);
    }

    public String setex(String key, int seconds, String val) {
        return this.jedisCluster.setex(key, seconds, val);
    }

    public <T> T getT(String key, Class<T> clazz) {
        String val = this.jedisCluster.get(key);
        if (!StringUtils.isEmpty(val)) {
            T t = (T)JSONObject.parseObject(val, clazz);
            return t;
        }
        return null;
    }

    public <T> T hgetT(String key, String field, Class<T> clazz) {
        String val = this.jedisCluster.hget(key, field);
        if (!StringUtils.isEmpty(val)) {
            T t = (T)JSONObject.parseObject(val, clazz);
            return t;
        }
        return null;
    }

    public <T> List<T> hvalsT(String key, Class<T> clazz) {
        List<String> vals = this.jedisCluster.hvals(key);
        if (vals != null) {
            List<T> result = new ArrayList<>(vals.size());
            for (String val : vals) {
                if (!StringUtils.isEmpty(val)) {
                    T t = (T)JSONObject.parseObject(val, clazz);
                    result.add(t);
                }
            }
            return result;
        }
        return null;
    }

    public Long del(String key) {
        return this.jedisCluster.del(key);
    }

    public <T> String hmsetT(String key, List<T> list, Function<T, String> keyFunction) throws Exception {
        Map<String, String> hMap = new HashMap<>(list.size());
        for (T t : list) {
            if (t != null) {
                String field = keyFunction.apply(t);
                hMap.put(field, JSONObject.toJSONString(t));
            }
        }
        if (!hMap.isEmpty())
            return this.jedisCluster.hmset(key, hMap);
        return null;
    }

    public Long incrBy(String key, Long increment) {
        return this.jedisCluster.incrBy(key, increment.longValue());
    }

    public Long rpush(String key, String... vals) {
        return this.jedisCluster.rpush(key, vals);
    }

    public String lpop(String key) {
        return this.jedisCluster.lpop(key);
    }

    public Long setIfNotExist(String key, String value) {
        return this.jedisCluster.setnx(key, value);
    }

    public String rpoplpush(String srcKey, String destKey) {
        return this.jedisCluster.rpoplpush(srcKey, destKey);
    }

    public String brpoplpush(String srcKey, String destKey, int time) {
        return this.jedisCluster.brpoplpush(srcKey, destKey, time);
    }

    public Long lrem(String key, long count, String value) {
        return this.jedisCluster.lrem(key, count, value);
    }

    public List<String> lrange(String key, long start, long end) {
        return this.jedisCluster.lrange(key, start, end);
    }
}

