package com.crawler.spider.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class JDPriceInfo {
    /**
     * 目前价格
     */
    private String p;

    /**
     * 指导价
     */
    private String op;

    private String cbf;

    /**
     * 商品id
     */
    private String id;

    private String m;
}
