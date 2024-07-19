package org.example;

import com.dinstone.beanstalkc.BeanstalkClient;
import com.dinstone.beanstalkc.BeanstalkClientFactory;
import com.dinstone.beanstalkc.Configuration;
import com.dinstone.beanstalkc.Job;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static final String TEST_QUEUE = "test_queue";
    public static final int SLEEP_TIME =30000;


    public static void main(String[] args) throws InterruptedException {
        int concurrency = 100;
        int messagesNumber = 50000;

        Supplier<QueueFacade> redisNoPersistenceQueueSupplier = () -> new RedisQueueFacade("localhost", 6379, TEST_QUEUE);
        Supplier<QueueFacade> redisRdbQueueSupplier = () -> new RedisQueueFacade("localhost", 6380, TEST_QUEUE);
        Supplier<QueueFacade> redisAofQueueSupplier = () -> new RedisQueueFacade("localhost", 6381, TEST_QUEUE);
        Supplier<QueueFacade> beanstalkQueueSupplier = () -> new BeanstalkdQueueFacade("localhost", 11300, TEST_QUEUE);
        Supplier<QueueFacade> beanstalkPersistQueueSupplier = () -> new BeanstalkdQueueFacade("localhost", 11301, TEST_QUEUE);

        measurePerformance("Redis Queue (RDB)", redisRdbQueueSupplier, concurrency, messagesNumber);
        Thread.sleep(SLEEP_TIME);
        measurePerformance("Redis Queue (AOF)", redisAofQueueSupplier, concurrency, messagesNumber);
        Thread.sleep(SLEEP_TIME);
        measurePerformance("Redis Queue (no persistence)", redisNoPersistenceQueueSupplier, concurrency, messagesNumber);
        Thread.sleep(SLEEP_TIME);
        measurePerformance("Beanstalkd Queue (persist)", beanstalkPersistQueueSupplier, concurrency, messagesNumber);
        Thread.sleep(SLEEP_TIME);
        measurePerformance("Beanstalkd Queue", beanstalkQueueSupplier, concurrency, messagesNumber);
    }

    private static void measurePerformance(String testKey, Supplier<QueueFacade> queueFacadeSupplier, int concurrency, int messagesNumber) {
        ThreadLocal<QueueFacade> connectionPool = ThreadLocal.withInitial(queueFacadeSupplier);
        double writeTime = measureExecutionTime(connectionPool, queueFacade -> {
            String message = generateRandomString();
            queueFacade.push(message);
        }, concurrency, messagesNumber);
        double readTime = measureExecutionTime(connectionPool, queueFacade -> {
            String message = queueFacade.poll();
        }, concurrency, messagesNumber);
        System.out.printf(testKey + " - Write: %.2f events/s, Read: %.2f events/s%n", messagesNumber*1000/writeTime, messagesNumber*1000/readTime);
    }

    private static double measureExecutionTime(ThreadLocal<QueueFacade> connectionPool, Consumer<QueueFacade> command, int concurrency, int messagesNumber) {
        ExecutorService executor = Executors.newFixedThreadPool(concurrency);
        List<Callable<Void>> tasks = IntStream.range(0, messagesNumber)
                .mapToObj(i -> (Callable<Void>) () -> {
                    command.accept(connectionPool.get());
                    return null;
                })
                .collect(Collectors.toList());
        try {
            long startTime = System.currentTimeMillis();
            List<Future<Void>> result = executor.invokeAll(tasks);
            result.forEach(f -> {
                try {
                    f.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
            executor.shutdown();
            return System.currentTimeMillis() - startTime;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private static String generateRandomString() {
        byte[] array = new byte[7]; // length is bounded by 7
        new Random().nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }

}

interface QueueFacade {
    void push(String message);
    String poll();

    void destroy();

}

class RedisQueueFacade implements QueueFacade {

    private String queueName;
    private Jedis jedis;

    public RedisQueueFacade(String host, int port, String queueName) {
        this.queueName = queueName;
        jedis = createRedisClient(host, port);
    }

    public void destroy() {
        jedis.close();
    }

    @Override
    public void push(String message) {
        jedis.lpush(queueName, Thread.currentThread().getName() + " " + message);
    }

    @Override
    public String poll() {
        return jedis.brpop(5, queueName).get(0);
    }

    private static Jedis createRedisClient(String host, int port) {
        return new Jedis(host, port);
    }

}

class BeanstalkdQueueFacade implements QueueFacade {

    private BeanstalkClient beanstalkClient;

    public BeanstalkdQueueFacade(String host, int port, String queueName) {
        beanstalkClient = createBeanstalkClient(host, port);
        beanstalkClient.watchTube(queueName);
    }


    @Override
    public void push(String message) {
        message = Thread.currentThread().getName() + " " + message;
        beanstalkClient.putJob(0, 0, 600, message.getBytes());
    }

    @Override
    public String poll() {
        Job jobData = beanstalkClient.reserveJob(5);
        if (jobData != null) {
            // Assuming a method to convert the byte data to a job ID or message.
            // This part is based on the specific Beanstalkd library you're using.
            // String jobId = getJobIdFromData(jobData);
            // beanstalkClient.delete(jobId);

            // For our example, we'll just release the job.
            beanstalkClient.releaseJob(0, 0, 0);
            return new String(jobData.getData());
        }
        return null;
    }

    @Override
    public void destroy() {
        beanstalkClient.close();
    }

    private static BeanstalkClient createBeanstalkClient(String host, int port) {
        Configuration config = new Configuration();
        config.setServiceHost(host);
        config.setServicePort(port);
        config.setConnectTimeout(5000);
        config.setReadTimeout(5000);
        BeanstalkClientFactory factory = new BeanstalkClientFactory(config);
        return factory.createBeanstalkClient();
    }

}