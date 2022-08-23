package com.crawler.consumer.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("crawlerService")
public interface CrawlerClient {

    @GetMapping("/")
    String getHelloWord();

    @PostMapping("/task")
    void DispatchCrawlerTask(String url);

}
