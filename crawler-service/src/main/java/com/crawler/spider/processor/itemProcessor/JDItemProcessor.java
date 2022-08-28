package com.crawler.spider.processor.itemProcessor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class JDItemProcessor implements PageProcessor {
    /*
    商品信息
     */
    private String skuName;

    /**
     * 重试次数为3次，抓取间隔为一秒
     */
    private Site site = Site
            .me()
            .setRetryTimes(3)
            .setSleepTime(1000)
            .setCharset("utf-8")
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    @SneakyThrows
    @Override
    /**
     *@Description: process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
     *@Param: [page]
     *@return: void
     */
    public void process(Page page) {
        page.putField("skuName", page.getHtml().$(".sku-name", "text"));
        if (page.getResultItems().get("skuName") == null) {
            // skip this page
            page.setSkip(true);
            throw new Exception("获取商品详细信息失败！");
        } else {
            this.skuName = page.getResultItems().get("skuName").toString().trim();
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}
