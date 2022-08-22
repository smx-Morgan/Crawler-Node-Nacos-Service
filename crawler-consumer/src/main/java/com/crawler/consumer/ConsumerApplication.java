package com.crawler.consumer;

import com.crawler.consumer.clients.CrawlerClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableFeignClients
public class ConsumerApplication {

    static ConfigurableApplicationContext context;

    public static void postMain() {
        CrawlerClient bean = context.getBean(CrawlerClient.class);
        System.out.println(bean.getHelloWord());
    }


    public static void main(String[] args) {
        context = SpringApplication.run(ConsumerApplication.class, args);
        postMain();
    }


}
