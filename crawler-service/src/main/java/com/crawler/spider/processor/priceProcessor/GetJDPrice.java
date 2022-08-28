package com.crawler.spider.processor.priceProcessor;

import lombok.Data;
import lombok.NoArgsConstructor;
import us.codecraft.webmagic.Spider;


@Data
@NoArgsConstructor
public class GetJDPrice implements GetPrice {
    private JDPriceProcessor processor;

    @Override
    public String getPrice(String product_id) {
        String url = "https://p.3.cn/prices/mgets?skuIds=J_" + product_id;
        processor = new JDPriceProcessor();
        Spider.create(processor)
                .addUrl(url)
                .run(); // 执行爬虫
        return processor.getPriceInfo().getP();
    }
}
