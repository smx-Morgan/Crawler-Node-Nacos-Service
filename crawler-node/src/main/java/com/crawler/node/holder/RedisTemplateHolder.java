package com.crawler.node.holder;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class RedisTemplateHolder {
    public static RedisTemplateHolder bean;

    @Resource
    public StringRedisTemplate redis;

    public RedisTemplateHolder() {
        bean = this;
    }

    public void addList(String listKey, List<String> data) {
        redis.opsForList().leftPushAll(listKey, data);
    }


}
