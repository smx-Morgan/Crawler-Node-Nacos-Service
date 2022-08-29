package com.crawler.node.store;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Component
public class UrlStorage {
    @Resource
    StringRedisTemplate stringRedisTemplate;

    public void read(String url, String json) {
        stringRedisTemplate.opsForList().leftPush(url, json);
    }

    public void creatList(String url) {
        List list = stringRedisTemplate.opsForList().range(url, 0, 100);
    }

    public Set<String> getAllKey() {
        // 当前数据库key的数量
        return stringRedisTemplate.keys("*");
    }

    public void dele(String url) {
        stringRedisTemplate.delete(url);
    }
}
