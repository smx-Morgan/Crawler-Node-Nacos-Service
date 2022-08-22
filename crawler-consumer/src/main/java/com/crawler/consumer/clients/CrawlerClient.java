package com.crawler.consumer.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("crawlerService")
public interface CrawlerClient {

    @GetMapping()
    String getHelloWord();


}
