package com.crawler.nacos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("/")
    public String sayHello() {
        return "Hello! This is a crawler node";
    }

}
