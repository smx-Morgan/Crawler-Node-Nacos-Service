package com.crawler.spider.processor.itemProcessor;


import lombok.Data;
import lombok.NoArgsConstructor;
import us.codecraft.webmagic.Spider;


@Data
@NoArgsConstructor
public class GetJDItem implements GetItem {
    private JDItemProcessor processor;

    @Override
    public String getItemInfo(String productUrl) {
        processor = new JDItemProcessor();
        Spider.create(processor)
                .addUrl(productUrl)
//                .setDownloader(HttpUtil.getDownloader())
                .run();
        return processor.getSkuName();
    }
}
