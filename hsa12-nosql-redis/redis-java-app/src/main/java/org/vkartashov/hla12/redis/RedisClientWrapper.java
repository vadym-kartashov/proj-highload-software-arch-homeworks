package org.vkartashov.hla12.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vkartashov.hla12.RandomUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.params.SetParams;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

public class RedisClientWrapper {

    private static Logger LOG = LoggerFactory.getLogger(RedisClientWrapper.class);

    private JedisSentinelPool sentinelPool;
    private static double STAMPEDE_BETA = 0.5;
    private static double STAMPEDE_DELTA = 5;

    private static AtomicInteger cacheHits = new AtomicInteger(0);


    public RedisClientWrapper(JedisSentinelPool sentinelPool) {
        this.sentinelPool = sentinelPool;
    }

    public void set(String key, String value) {
        executeInJedis(jedis -> {
            jedis.set(key, value);
            return null;
        });
    }

    public void setAll(Map<String, String> dataSet) {
        for (Map.Entry<String, String> entry : dataSet.entrySet()) {
            set(entry.getKey(), entry.getValue(), Integer.parseInt(entry.getKey()) + 1);
        }
    }

    public void setAll(Map<String, String> dataSet, int ttl) {
        for (Map.Entry<String, String> entry : dataSet.entrySet()) {
            set(entry.getKey(), entry.getValue(), ttl);
        }
    }

    public void set(String key, String value, int ttl) {
        executeInJedis(jedis -> {
            jedis.set(key, value, SetParams.setParams().ex(ttl));
            return null;
        });
    }

    public Set<String> getAllKeys() {
        return executeInJedis(jedis -> jedis.keys("*"));
    }

    public String get(String key) {
        return executeInJedis(jedis -> jedis.get(key));
    }

    public void removeAll() {
        executeInJedis(jedis -> {
            jedis.flushAll();
            return null;
        });
    }

    public void setEvictionPolicy(RedisEvictionPolicy policy) {
        executeInJedis(jedis -> {
            jedis.configSet("maxmemory-policy", policy.getPolicy());
            return null;
        });
    }

    public String getEvictionPolciy() {
        return executeInJedis(jedis -> jedis.configGet("maxmemory-policy").toString());
    }

    public String probibalisticGet(String key, int ttl, Callable<String> valueSupplier) {
        return executeInJedis(jedis -> {
            boolean recalculated = false;
            String value = jedis.get(key);
            long remainingTtl = jedis.ttl(key);
            if (value == null) {
                LOG.info("Value expired or not persisted in redis");
            }
            if (value == null || isProbabilisticallyExpired(remainingTtl)) {
                synchronized (key) {
                    value = jedis.get(key);
                    remainingTtl = jedis.ttl(key);
                    if (value == null) {
                        LOG.info("Value expired or not persisted in redis");
                    }
                    if (value == null || isProbabilisticallyExpired(remainingTtl)) {
                        try {
                            value = valueSupplier.call();
                            recalculated = true;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        jedis.set(key, value, SetParams.setParams().ex(ttl));
                    }
                }
            }
            if (!recalculated) {
                cacheHits.incrementAndGet();
            } else {
                LOG.info("Value recalculated cacheHits: " + cacheHits.get());
            }
            return value;
        });
    }

    private boolean isProbabilisticallyExpired(long remainingTtl) {

        BigDecimal timeInSeconds = new BigDecimal(System.currentTimeMillis()).divide(BigDecimal.valueOf(1000));
        BigDecimal expiry = timeInSeconds.add(new BigDecimal(remainingTtl));
        boolean result = timeInSeconds.subtract(new BigDecimal(STAMPEDE_DELTA * STAMPEDE_BETA * Math.log(Math.random()))).compareTo(expiry) > 0;
        return result;
    }

    private <R> R executeInJedis(Function<Jedis, R> command) {
        Jedis jedis = null;
        try {
            jedis = sentinelPool.getResource();
            return command.apply(jedis);
        } catch (Exception e) {
            LOG.error("Error while executing command in jedis", e);
            throw e;
        } finally {
            if (jedis != null) {
                sentinelPool.returnResource(jedis);
            }
        }
    }

}
