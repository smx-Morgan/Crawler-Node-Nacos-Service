package com.crawler.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;
import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * 工具类，封装HBase的配置和连接
 * 如果要改配置，只需要修改src/main/resources/hbase-config.properties
 */
public class HBaseUtil {
    public static final ResourceBundle bundle = ResourceBundle.getBundle("hbase-config");
    public static final Configuration config;

    static {
        config = HBaseConfiguration.create();
        Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            config.set(key, bundle.getString(key));
        }
    }

    public static Configuration getConfiguration() {
        return config;
    }

    public static Connection getConnection() throws IOException {
        return ConnectionFactory.createConnection(getConfiguration());
    }


}
