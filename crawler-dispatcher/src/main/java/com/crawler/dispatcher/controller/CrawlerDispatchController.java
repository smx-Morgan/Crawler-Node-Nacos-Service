package com.crawler.dispatcher.controller;

import com.crawler.api.entity.TaskWrapper;
import com.crawler.dispatcher.api.CrawlerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

@RestController
@Slf4j
public class CrawlerDispatchController {
    @Resource
    CrawlerClient client;
    @Resource
    StringRedisTemplate redis;

    @GetMapping("/")
    public String getStatus() {
        return client.getStatus();
    }

    @PostMapping("/search")
    public void dispatchSearchTask(String url) {
        createTaskAndDispatch(url);
    }

    @PostMapping("/item")
    public void dispatchItemTask(String json) {
        createTaskAndDispatch(json);
    }

    void createTaskAndDispatch(String urlOrJson) {
        TaskWrapper itemTask = TaskWrapper.newTask(urlOrJson);
        registerTaskOnRedis(itemTask);
        CompletableFuture.runAsync(() -> {
            client.dispatchItemTask(itemTask.toJson());
            log.info("发送任务：{}", itemTask);
        });
    }

    void registerTaskOnRedis(TaskWrapper task) {
        redis.opsForHash().put("crawlerTask", task.toJson(), "false");
    }
}
