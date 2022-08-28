package com.crawler.hbase;

import org.apache.hadoop.hbase.client.Connection;

import java.io.IOException;

public class HBaseConnection {
    public static Connection conn = null;

    static {
        try {
            // 创建连接
            conn = HBaseUtil.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}





