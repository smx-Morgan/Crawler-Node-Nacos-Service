package com.crawler.nacos.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
public class BootstrapController {

    /**
     * 本方法用于接收主节点分配的url，并将url传递给爬虫系统进行工作
     *
     * @param url 主节点分配的url
     */
    @PostMapping("/task")
    public void receiveCrawlerTask(String url) {
        // todo
        log.debug("收到URL任务：{}", url);
    }

}
