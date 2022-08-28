package com.crawler.node.store;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

@Component
public class UrlStorage {
    @Resource
    StringRedisTemplate stringRedisTemplate;

    public void read(String url, String json) {
        stringRedisTemplate.opsForValue().set(url, json);
    }

    public Set<String> getAllKey() {
        // 当前数据库key的数量
        return stringRedisTemplate.keys("*");
    }

    public void dele(String url) {
        stringRedisTemplate.delete(url);
    }
}
