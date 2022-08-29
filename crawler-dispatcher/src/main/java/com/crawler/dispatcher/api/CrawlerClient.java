package com.crawler.dispatcher.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("crawlerNode")
public interface CrawlerClient {
    @GetMapping("/")
    String getStatus();

    @PostMapping("/search")
    void dispatchSearchTask(@RequestBody String taskWrapper);

    @PostMapping("/item")
    void dispatchItemTask(@RequestBody String taskWrapper);


}
