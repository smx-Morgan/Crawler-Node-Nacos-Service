package com.crawler.spider.util;


import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HttpUtil {
    // log4j日志记录，这里主要用于记录网页下载时间的信息
    // IP地址代理库
    /*
    ProxyProvider定位更多是一个“组件”，所以代理不再从Site设置，而是由HttpClientDownloader设置。
     */
    private static final HttpClientDownloader httpClientDownloader = new HttpClientDownloader();

    /*
     * 初次使用时使用静态代码块将IP代理库加载进set中
     */
    static {
        InputStream in = HttpUtil.class.getClassLoader().getResourceAsStream("IPProxyRepository.txt");  // 加载包含代理IP的文本
        // 构建缓冲流对象
        InputStreamReader isr = new InputStreamReader(in);
        BufferedReader bfr = new BufferedReader(isr);
        String line = null;
        List<Proxy> proxiesTemp = new ArrayList<>();
        try {
            // 循环读每一行，添加进map中
            while ((line = bfr.readLine()) != null) {
                String[] split = line.split(":");   // 以:作为分隔符，即文本中的数据格式应为192.168.1.1:4893:username:pwd
                String host = split[0];
                int port = Integer.parseInt(split[1]);
                String username = split[2];
                String pwd = split[3];
                proxiesTemp.add(new Proxy(host, port, username, pwd));
            }
            httpClientDownloader.setProxyProvider(new SimpleProxyProvider(Collections.unmodifiableList(proxiesTemp)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成Site
     *
     * @return
     */
    public static Site getSite() {
        return Site.me()
                .setRetryTimes(3)
                .setSleepTime(1000)
                .setCharset("utf-8")
                .setUserAgent(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.75 Safari/537.36");
    }

    public static HttpClientDownloader getDownloader() {
        return httpClientDownloader;
    }
}
