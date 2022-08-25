package com.crawler.consumer.controller;

import com.crawler.consumer.clients.CrawlerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class CrawlerClientController {

    @Resource
    CrawlerClient client;

    @GetMapping("/getCrawlerHello")
    public String getCrawlerHello() {
        System.out.println("\n\n <<<<<<<<< feign远程调用 >>>>>>>>>>\n\n");
        System.out.println(client);
        return client.getHelloWord();
    }

    @PostMapping("/task")
    public String getCrawlerHello(String url) {
        log.debug(">>>>>>>>>>>>>>>>>>向爬虫节点提交URL：{}", url);
        client.DispatchCrawlerTask(url);
        return client.getHelloWord();
    }

}
