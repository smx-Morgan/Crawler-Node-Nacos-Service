package com.crawler.node.util;


import com.crawler.node.store.UrlStorage;
import com.google.gson.Gson;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Component
public class FlinkApi {
    public static FlinkApi bean;
    @Resource
    private UrlStorage urlStorage;

    public FlinkApi() {
        bean = this;
    }

    public List<Tuple2<String, Integer>> getUrls() {
        Set<String> allKey = urlStorage.getAllKey();

        List<String> allKeyList = new LinkedList<>(allKey);
        List<Tuple2<String, Integer>> urls = new LinkedList<>();

        for (String s : allKeyList) {
            Random r = new Random();
            // 随机产生0，1
            int id = r.nextInt(2);
            urls.add(new Tuple2<>(s, id));
        }

        // 清空redis的key
        for (Tuple2<String, Integer> key : urls) {
            urlStorage.dele(key.f0);
        }

        return urls;
    }

    // 筛选标识为0或1的url
    public void filter(int id) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<List<Tuple2<String, Integer>>> urlStreams = env.fromElements(getUrls());

        SingleOutputStreamOperator<List<Tuple2<String, Integer>>> urls = urlStreams.filter(
                (FilterFunction<List<Tuple2<String, Integer>>>) tuple2s -> {
                    for (Tuple2<String, Integer> tuple2 : tuple2s) {
                        if (tuple2.f1 == id) {
                            return true;
                        }
                    }
                    return false;
                });
        urls.print();
        env.execute();
    }

    // 将一条url写入redis
    public void writeToRedis(String jsonUrl) {
        // 解析json字符串
        Gson gson = new Gson();
        String url = gson.fromJson("url", String.class);
        urlStorage.read(url, jsonUrl);
    }


}
