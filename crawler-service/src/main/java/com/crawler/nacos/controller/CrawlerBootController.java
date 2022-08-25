package com.crawler.nacos.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class CrawlerBootController {

    /**
     * 本方法用于接收主节点分配的url，并将url传递给爬虫系统进行工作
     *
     * @param url 主节点分配的url
     */
    @PostMapping("/task")
    @ResponseBody
    public String receiveCrawlerTask(String url) {
        // todo
        log.info(">>>>>>>>>>>>>>>>>>爬虫节点收到URL任务：{}", url);
        return "Success";
    }

}
