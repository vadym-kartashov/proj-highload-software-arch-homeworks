package org.vkartashov.hla12.redis;

public enum RedisEvictionPolicy {


    VOLATILE_LRU("volatile-lru"),
    ALLKEYS_LRU("allkeys-lru"),
    VOLATILE_LFU("volatile-lfu"),
    ALLKEYS_LFU("allkeys-lfu"),
    VOLATILE_RANDOM("volatile-random"),
    ALLKEYS_RANDOM("allkeys-random"),
    VOLATILE_TTL("volatile-ttl"),
    NO_EVICTION("noeviction");

    private final String policy;
    private boolean isLru;
    private boolean isLfu;

    RedisEvictionPolicy(String policy) {
        this.isLfu = policy.contains("lfu");
        this.isLru = policy.contains("lru");
        this.policy = policy;
    }

    public String getPolicy() {
        return policy;
    }

    public boolean isLru() {
        return isLru;
    }

    public boolean isLfu() {
        return isLfu;
    }
}
