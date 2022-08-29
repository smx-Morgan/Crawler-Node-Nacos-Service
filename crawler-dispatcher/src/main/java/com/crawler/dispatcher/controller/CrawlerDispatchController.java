package com.crawler.dispatcher.controller;

import com.crawler.api.entity.TaskWrapper;
import com.crawler.dispatcher.api.CrawlerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

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
    public void dispatchSearchTask(@RequestParam("url") String url) {
        createTaskAndDispatch(url, client::dispatchSearchTask);
    }

    @PostMapping("/item")
    public void dispatchItemTask(@RequestParam("json") String json) {
        createTaskAndDispatch(json, client::dispatchItemTask);
    }

    void createTaskAndDispatch(String urlOrJson, Consumer<String> client) {
        TaskWrapper task = TaskWrapper.newTask(urlOrJson);

        registerTaskOnRedis(task);
        CompletableFuture.runAsync(() -> {
            client.accept(task.toJson());
            log.info("发送任务：{}", task);
        });
    }

    void registerTaskOnRedis(TaskWrapper task) {
        redis.opsForHash().put("crawlerTask", task.toJson(), "false");
    }
}
