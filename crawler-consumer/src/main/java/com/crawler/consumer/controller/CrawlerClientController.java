package com.crawler.consumer.controller;

import com.crawler.consumer.clients.CrawlerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class CrawlerClientController {

    @Resource
    CrawlerClient bean;

    @GetMapping("/getCrawlerHello")
    public String getCrawlerHello() {
        System.out.println("\n\n <<<<<<<<< feign远程调用 >>>>>>>>>>\n\n");
        System.out.println(bean);
        return bean.getHelloWord();
    }
}
