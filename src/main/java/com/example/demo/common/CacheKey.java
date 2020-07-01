package com.example.demo.common;

public enum CacheKey {
    HASH_KEY("miaosha_hash"),
    LIMIT_KEY("miaosha_limit");

    private String key;

    private CacheKey(String key) {
        this.key = key;
    }
    public String getKey() {
        return key;
    }
}
