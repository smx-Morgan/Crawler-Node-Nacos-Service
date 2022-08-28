package com.crawler.spider.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchItemInfo implements Serializable {
    /*
    商品集合id
     */
    private String spu;
    /*
    商品最小品类id
     */
    private String sku;
    /*
    商品图片
     */
    private String pic;
    /*
    商品详细地址
     */
    private String url;
}
