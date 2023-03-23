package com.example.agent.util;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisUtil{

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置key-value
     *
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 获取key对应的value
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除key
     *
     * @param key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }


    public void appendInteger(String key, Integer i){
        redisTemplate.opsForList().rightPushAll(key,i);
    }

    public List<Object> getList(String key){
        return redisTemplate.opsForList().range(key,0,-1);
    }

}
