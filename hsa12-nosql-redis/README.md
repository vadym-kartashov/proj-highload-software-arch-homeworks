# Highload Software Architecture homeworks #12 Redis Cluster
Task: Try all eviction strategies  Write a wrapper for Redis Client that implement probabilistic cache

## Benchmarking
According to benchmarking result the highest RPS has been preserved with concurrency = 30<br/>

| Operation                          | rps (1c) | avg_msec (1c) | rps (10c) | avg_msec (10c) | rps (30c) | avg_msec (30c) | rps (50c) | avg_msec (50c) | rps (100c) | avg_msec (100c)|
|------------------------------------| --- | --- | --- | --- | --- | --- | --- | --- | --- | ---|
 | Overall Average                    | 13327.52 | 0.07 | 86068.06 | 0.1 | 104068.7 | 0.24 | 102073.41 | 0.38 | 101964.92 | 0.79|
 | GET                                | 17112.0 | 0.05 | 90556.0 | 0.07 | 136208.0 | 0.12 | 110176.0 | 0.24 | 137187.3 | 0.37|
 | HSET                               | 12524.0 | 0.07 | 111525.9 | 0.06 | 121212.0 | 0.14 | 116980.0 | 0.23 | 111156.0 | 0.48|
 | INCR                               | 12232.0 | 0.08 | 102980.0 | 0.07 | 115972.0 | 0.15 | 118896.0 | 0.23 | 117860.0 | 0.44|
 | LPOP                               | 12224.0 | 0.08 | 110184.0 | 0.06 | 116520.0 | 0.14 | 112856.6 | 0.24 | 120300.0 | 0.43|
 | LPUSH                              | 12336.0 | 0.07 | 103684.0 | 0.07 | 118103.6 | 0.14 | 116760.0 | 0.23 | 113824.7 | 0.46|
 | LPUSH (needed to benchmark LRANGE) | 12604.0 | 0.07 | 107340.0 | 0.07 | 115152.0 | 0.15 | 117876.5 | 0.23 | 117116.0 | 0.44|
 | LRANGE_100 (first 100 elements)    | 15080.0 | 0.06 | 64268.0 | 0.1 | 72552.0 | 0.23 | 71147.4 | 0.37 | 70023.9 | 0.73|
 | LRANGE_300 (first 300 elements)    | 10844.0 | 0.06 | 26248.0 | 0.22 | 29836.0 | 0.52 | 31040.0 | 0.82 | 23980.1 | 2.19|
 | LRANGE_500 (first 500 elements)    | 8408.0 | 0.07 | 20015.9 | 0.27 | 20544.0 | 0.74 | 20561.8 | 1.23 | 19928.6 | 2.53|
 | LRANGE_600 (first 600 elements)    | 7900.0 | 0.07 | 16322.7 | 0.33 | 16725.1 | 0.91 | 17502.0 | 1.44 | 16571.4 | 3.03|
 | MSET (10 keys)                     | 11708.0 | 0.08 | 75316.0 | 0.11 | 85192.0 | 0.29 | 96540.0 | 0.44 | 94600.0 | 0.91|
 | PING_INLINE                        | 14480.0 | 0.06 | 87952.0 | 0.08 | 138252.0 | 0.12 | 114848.6 | 0.23 | 140260.0 | 0.36|
 | PING_MBULK                         | 18180.0 | 0.05 | 88016.0 | 0.08 | 142140.0 | 0.11 | 132180.0 | 0.2 | 136052.0 | 0.38|
 | RPOP                               | 12380.0 | 0.07 | 115140.0 | 0.06 | 120968.0 | 0.14 | 118468.0 | 0.23 | 116514.0 | 0.44|
 | RPUSH                              | 11596.0 | 0.08 | 108924.0 | 0.06 | 115228.0 | 0.14 | 119167.3 | 0.22 | 115255.0 | 0.45|
 | SADD                               | 15428.0 | 0.06 | 112380.0 | 0.06 | 120008.0 | 0.14 | 120708.0 | 0.23 | 119472.0 | 0.43|
 | SET                                | 12292.0 | 0.07 | 117992.0 | 0.06 | 121713.2 | 0.14 | 115508.0 | 0.24 | 112621.5 | 0.46|
 | SPOP                               | 18860.0 | 0.05 | 88352.0 | 0.08 | 138152.0 | 0.12 | 131424.0 | 0.2 | 102956.0 | 0.52|
 | ZADD                               | 11972.0 | 0.08 | 84304.0 | 0.09 | 117276.0 | 0.16 | 117976.0 | 0.24 | 116360.0 | 0.46|
 | ZPOPMIN                            | 18390.4 | 0.05 | 89860.6 | 0.07 | 119620.0 | 0.14 | 140852.0 | 0.19 | 137260.0 | 0.37|

## Building master slave cluster with sentinel

Cluster consists of 1 sentinel, 1 master and one slave. Cluster discovery is done through sentinel <br/>

## Eviction strategies
Max memory policy has been set to ```--maxmemory 50mb```

| Policy Name     | Description                                                     |
|-----------------|-----------------------------------------------------------------|
| volatile-lru    | Evict using approximated LRU among the keys with an expire set. |
| allkeys-lru     | Evict any key using approximated LRU.                           |
| volatile-lfu    | Evict using approximated LFU among the keys with an expire set. |
| allkeys-lfu     | Evict any key using approximated LFU.                           |
| volatile-random | Remove a random key among the ones with an expire set.          |
| allkeys-random  | Remove a random key, any key.                                   |
| volatile-ttl    | Remove the key with the nearest expire time (minor TTL) |
| noeviction      | Don't evict anything, just return an error on write operations. |

Fill cache
Each 10 records check evicted records <br/>
Injected keyset is following: [1,2,3] , ttl is the same as key <br/>
Record 1 has 15 read count, inserted as first
Record 2 has 10 read count, inserted as second
Record 3 has 5 read count, inserted as third

```
hla12-redis-java-app-1  | SUCCESS volatile-lru [3, 4]
hla12-redis-java-app-1  | EVICTED KEYS: [1, 2] - Least recently used ones are evicted
hla12-redis-java-app-1  | SUCCESS allkeys-lru [3, 4]
hla12-redis-java-app-1  | EVICTED KEYS: [1, 2] - Least recently used ones are evicted
hla12-redis-java-app-1  | SUCCESS volatile-lfu [1, 4]
hla12-redis-java-app-1  | EVICTED KEYS: [2, 3] - Least frequently used ones are evicted
hla12-redis-java-app-1  | SUCCESS allkeys-lfu [1, 4]
hla12-redis-java-app-1  | EVICTED KEYS: [2, 3] - Least frequently used ones are evicted 
hla12-redis-java-app-1  | SUCCESS volatile-random [1, 4]
hla12-redis-java-app-1  | EVICTED KEYS: [2, 3] - random are evicted within ttl set
hla12-redis-java-app-1  | SUCCESS allkeys-random [2, 4]
hla12-redis-java-app-1  | EVICTED KEYS: [1, 3] - random are evicted
hla12-redis-java-app-1  | SUCCESS volatile-ttl [3, 4]
hla12-redis-java-app-1  | EVICTED KEYS: [1, 2] - evicted by ttl
hla12-redis-java-app-1  | ERROR noeviction OOM command not allowed when used memory > 'maxmemory'. 3 - error since no keys should be evicted
```

## Probabilistic cache

Implementation according to [Cache stampede](https://en.wikipedia.org/wiki/Cache_stampede) <br/>
Implementation [RedisClientWrapper](./src/main/java/ru/otus/hw12redis/redis/RedisClientWrapper.java) <br/>
Detla = 5 , should be set depending on request execution time <br/>
Beta = 0.5 <br/>
TTL = 30 sec <br/>

As we see from logs value is requested once per ~ 15-20 seconds 

```
2023-08-28 15:27:00 12:27:00.749 [pool-1-thread-1] INFO  o.v.hla12.redis.RedisClientWrapper - Value expired or not persisted in redis
2023-08-28 15:27:00 12:27:00.755 [pool-1-thread-1] INFO  o.v.hla12.redis.RedisClientWrapper - Value recalculated cacheHits: 0
2023-08-28 15:27:17 12:27:17.983 [pool-1-thread-1] INFO  o.v.hla12.redis.RedisClientWrapper - Value recalculated cacheHits: 92774
2023-08-28 15:27:35 12:27:35.750 [pool-1-thread-1] INFO  o.v.hla12.redis.RedisClientWrapper - Value recalculated cacheHits: 206786
2023-08-28 15:27:54 12:27:54.299 [pool-1-thread-1] INFO  o.v.hla12.redis.RedisClientWrapper - Value recalculated cacheHits: 325167
2023-08-28 15:28:13 12:28:13.951 [pool-1-thread-1] INFO  o.v.hla12.redis.RedisClientWrapper - Value recalculated cacheHits: 450966
2023-08-28 15:28:31 12:28:31.569 [pool-1-thread-1] INFO  o.v.hla12.redis.RedisClientWrapper - Value recalculated cacheHits: 563557
2023-08-28 15:28:51 12:28:51.296 [pool-1-thread-1] INFO  o.v.hla12.redis.RedisClientWrapper - Value recalculated cacheHits: 690000
2023-08-28 15:29:07 12:29:07.907 [pool-1-thread-1] INFO  o.v.hla12.redis.RedisClientWrapper - Value recalculated cacheHits: 795691
2023-08-28 15:29:28 12:29:28.250 [pool-1-thread-1] INFO  o.v.hla12.redis.RedisClientWrapper - Value recalculated cacheHits: 925654
2023-08-28 15:29:46 12:29:46.741 [pool-1-thread-1] INFO  o.v.hla12.redis.RedisClientWrapper - Value recalculated cacheHits: 1043293
2023-08-28 15:30:06 12:30:06.013 [pool-1-thread-1] INFO  o.v.hla12.redis.RedisClientWrapper - Value recalculated cacheHits: 1165953
2023-08-28 15:30:25 12:30:25.406 [pool-1-thread-1] INFO  o.v.hla12.redis.RedisClientWrapper - Value recalculated cacheHits: 1289782
2023-08-28 15:30:41 12:30:41.857 [pool-1-thread-1] INFO  o.v.hla12.redis.RedisClientWrapper - Value recalculated cacheHits: 1393939
```