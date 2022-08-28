package com.crawler.dispatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CrawlerDispatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlerDispatchApplication.class, args);
    }

}
