package com.sake.examination_system.service.Imp;

import com.sake.examination_system.service.RedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImp implements RedisService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void setValue(String key, Object value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key and value must not be null.");
        }
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setValueWithExpire(String key, Object value, long expireSeconds) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key and value must not be null.");
        }
        redisTemplate.opsForValue().set(key, value, expireSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Long getRemainingExpireTime(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null.");
        }

        Long remainingTime = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return remainingTime != null ? remainingTime : 0L;
    }

    @Override
    public Object getValue(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null.");
        }
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteValue(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null.");
        }
        redisTemplate.delete(key);
    }

    @Override
    public boolean hasKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null.");
        }
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

}

