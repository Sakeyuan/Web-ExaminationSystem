package com.sake.examination_system.service;

public interface RedisService {
    void setValue(String key, Object value);
    void setValueWithExpire(String key, Object value, long expireSeconds);
    Long getRemainingExpireTime(String key);
    Object getValue(String key);
    void deleteValue(String key);
    boolean hasKey(String key);
}
