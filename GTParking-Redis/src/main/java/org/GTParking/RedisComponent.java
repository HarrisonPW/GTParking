package org.GTParking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisComponent {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void setKey(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
    public Boolean containsKey(String key) {
        return redisTemplate.hasKey(key);
    }
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }
    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
