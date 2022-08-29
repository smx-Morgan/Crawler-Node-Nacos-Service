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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.crawler.dispatcher.config.Constants.KEY_HASH_CRAWLER_TASK;

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
        TaskWrapper task = createTaskAndDispatch(url, client::dispatchSearchTask);

        // 要开启一个异步线程，专门轮询爬虫是否处理完了search任务
        // 如果爬虫处理完了，就要调用flink
        CompletableFuture.runAsync(() -> waitSearchResult(task));
    }

    @PostMapping("/item")
    public void dispatchItemTask(@RequestParam("json") String json) {
        createTaskAndDispatch(json, client::dispatchItemTask);
    }

    TaskWrapper createTaskAndDispatch(String urlOrJson, Consumer<String> client) {
        TaskWrapper task = TaskWrapper.newTask(urlOrJson);

        registerTaskOnRedis(task);
        CompletableFuture.runAsync(() -> {
            client.accept(task.toJson());
            log.info("发送任务：{}", task);
        });

        return task;
    }

    void registerTaskOnRedis(TaskWrapper task) {
        redis.opsForHash().put(KEY_HASH_CRAWLER_TASK, task.toJson(), "false");
    }

    void waitSearchResult(TaskWrapper task) {
        while (true) {
            boolean done = Boolean.parseBoolean((String) redis.opsForHash().get(KEY_HASH_CRAWLER_TASK, task.getUuid()));
            if (!done) {
                Thread.yield();
            } else {
                break;
            }
        }

        // 调用flink
        List<String> jsonList = redis.opsForList().range(task.getMetaData(), 0, -1);

        if (jsonList == null || jsonList.isEmpty()) {
            return;
        }

        jsonList.forEach(this::dispatchItemTask);
    }
}
