package org.vkartashov.hla12;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vkartashov.hla12.redis.RedisClientWrapper;
import org.vkartashov.hla12.redis.RedisEvictionPolicy;
import redis.clients.jedis.JedisSentinelPool;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class RedisJavaAppApplication{

	private static final AtomicInteger dbSimulatedReturnValue = new AtomicInteger(0);

	private static final Logger LOG = LoggerFactory.getLogger(RedisJavaAppApplication.class);

	public static void main(String[] args) {
		RedisClientWrapper clientWrapper = preapreRedisClientWrapper();
		prepareEvictionStrategyRoutine(clientWrapper);
	}

	private static void prepareCacheStampedeRoutine(RedisClientWrapper clientWrapper) throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(8);
		executor.execute(() -> {
            while (true) {
                clientWrapper.probibalisticGet("KEY", 30, () -> String.valueOf(dbSimulatedReturnValue.incrementAndGet()));
            }
        });

		Thread.sleep(120000);
		executor.shutdownNow();
	}

	private static void prepareEvictionStrategyRoutine(RedisClientWrapper clientWrapper) {
		clientWrapper.setEvictionPolicy(RedisEvictionPolicy.ALLKEYS_RANDOM);
		Map<String, String> dataSet = new HashMap<>();
		dataSet.put("1" , RandomUtil.generateRandomString(600000));
		dataSet.put("2" , RandomUtil.generateRandomString(600000));
		dataSet.put("3" , RandomUtil.generateRandomString(600000));
		List<String> result = new ArrayList<>();
		for (RedisEvictionPolicy value : RedisEvictionPolicy.values()) {
			try {
				clientWrapper.setEvictionPolicy(value);

				clientWrapper.setAll(dataSet);
				IntStream.range(0, 20).forEach(i -> clientWrapper.get("1"));
				IntStream.of(0, 10).forEach(i -> clientWrapper.get("2"));
				IntStream.of(0, 5).forEach(i -> clientWrapper.get("3"));
				clientWrapper.set("4" , RandomUtil.generateRandomString(800000));

				Set<String> keys = clientWrapper.getAllKeys();
				result.add("SUCCESS " + value.getPolicy() + " " + keys.toString());
				Set<String> evictedKeys = new TreeSet<>(StringAsIntComparator.COMPARATOR);
				evictedKeys.addAll(dataSet.keySet());
				evictedKeys.removeAll(keys);
				result.add("EVICTED KEYS: " + evictedKeys);
			} catch (Exception e) {
				Set<String> keys = clientWrapper.getAllKeys();
				result.add("ERROR " + value.getPolicy() + " " + e.getMessage() + " " + keys.size());
			} finally {
				clientWrapper.removeAll();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		result.forEach(System.out::println);
	}

	private static RedisClientWrapper preapreRedisClientWrapper() {
		Set<String> sentinels = new HashSet<>();
		sentinels.add("redis-sentinel:26379"); // This should match your sentinel's address
		JedisSentinelPool sentinelPool = new JedisSentinelPool("mymaster", sentinels);
		return new RedisClientWrapper(sentinelPool);
	}

}
