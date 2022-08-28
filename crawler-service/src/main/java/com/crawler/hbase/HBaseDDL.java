package com.crawler.hbase;

import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HBaseDDL {

    public static Connection connection = HBaseConnection.conn;

    /**
     * 创建命名空间
     *
     * @param namespace 命名空间名称
     */
    public static void createNamespace(String namespace) throws IOException {
        // 获取admin
        Admin admin = connection.getAdmin();

        // 创建命名空间描述的建造者
        NamespaceDescriptor.Builder builder = NamespaceDescriptor.create(namespace);

        // 给命名空间添加需求
        builder.addConfiguration("user", "crawlSystem");

        // 使用builder构造出对应的添加完参数对象 完成创建

        try {
            admin.createNamespace(builder.build());
        } catch (IOException e) {
            // 命名空间已存在则会产生异常
            System.out.println("命名空间" + namespace + "已存在 " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            e.printStackTrace();
            throw new RuntimeException(e);
        }


        // 关闭admin
        admin.close();
    }

    /**
     * 判断命名空间是否存在
     *
     * @param namespace 命名空间名称
     * @return true表示存在
     */
    public static boolean namespaceExists(String namespace) throws IOException {
        Admin admin = connection.getAdmin();
        NamespaceDescriptor[] namespaceDescriptors = admin.listNamespaceDescriptors();
        for (NamespaceDescriptor descriptor : namespaceDescriptors) {
            if (namespace.equals(descriptor.getName())) {
                return true;
            }
        }
        admin.close();
        return false;
    }

    /**
     * 判断表格是否存在
     *
     * @param namespace 命名空间
     * @param tableName 表格名称
     * @return true 表示存在
     */
    public static boolean isTableExists(String namespace, String tableName) throws IOException {
        Admin admin = connection.getAdmin();
        boolean b = false;
        try {
            b = admin.tableExists(TableName.valueOf(namespace, tableName));
        } catch (IOException e) {
            e.printStackTrace();
            // throw new RuntimeException(e);
        }
        admin.close();
        return b;

    }

    /***
     * 创建新表格
     * @param namespace 命名空间
     * @param tableName 表格名称
     * @param columnFamilies 列族名称 可以有多个
     */
    public static void createTable(String namespace, String tableName, String... columnFamilies) throws IOException {

        Admin admin = connection.getAdmin();

        // 创建表格的建造者
        TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(TableName.valueOf(namespace, tableName));

        // 添加参数
        for (String columnFamily : columnFamilies) {
            // 创建列族描述
            ColumnFamilyDescriptorBuilder columnFamilyDescriptorBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(columnFamily));
            // 为当前的列族设置版本数
            columnFamilyDescriptorBuilder.setMaxVersions(3);
            // 设置TTL，定时删除（1h）
            columnFamilyDescriptorBuilder.setTimeToLive(3600);

            tableDescriptorBuilder.setColumnFamily(columnFamilyDescriptorBuilder.build());

        }

        try {
            admin.createTable(tableDescriptorBuilder.build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        admin.close();
    }

    /**
     * 修改表格其中一个列族的版本
     *
     * @param namespace    命名空间
     * @param tableName    表格名称
     * @param columnFamily 列族名称
     * @param version      版本
     */
    public static void modifyTableVersion(String namespace, String tableName, String columnFamily, int version) throws IOException {
        // 判断表格是否存在
        if (!isTableExists(namespace, tableName)) {
            return;
        }

        Admin admin = connection.getAdmin();

        try {
            TableDescriptor descriptor = admin.getDescriptor(TableName.valueOf(namespace, tableName));

            // 获取之前的表格表述，不是新建一个表格描述
            ColumnFamilyDescriptor columnFamily1 = descriptor.getColumnFamily(Bytes.toBytes(columnFamily));

            // 对应建造者进行表格数据的修改
            ColumnFamilyDescriptorBuilder columnFamilyDescriptorBuilder = ColumnFamilyDescriptorBuilder.newBuilder(columnFamily1);
            // 修改对应的版本
            columnFamilyDescriptorBuilder.setMaxVersions(version);

            TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(descriptor);

            tableDescriptorBuilder.modifyColumnFamily(columnFamilyDescriptorBuilder.build());

            admin.modifyTable(tableDescriptorBuilder.build());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }


    /**
     * 删除表格
     *
     * @param namespace 命名空间名称
     * @param tableName 表格名称
     * @return true表示删除成功
     */
    public static boolean deleteTable(String namespace, String tableName) throws IOException {
        // 判断表格是否存在
        if (!isTableExists(namespace, tableName)) {
            System.out.println("表格不存在 无法删除");
            return false;
        }
        Admin admin = connection.getAdmin();
        try {
            // HBase要求删除表格之前必须把表格标记为不可用，才允许删掉
            TableName tableName1 = TableName.valueOf(namespace, tableName);
            admin.disableTable(tableName1);
            admin.deleteTable(tableName1);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        admin.close();
        return true;
    }


    public static void main(String[] args) throws IOException {
        // 测试创建命名空间
        // createNamespace("TestAllAtOnce3");
        // 测试判断命名空间是否存在
        // System.out.println(namespaceExists("JDProducts")+" 存在");
        // createTable("TestAllAtOnce3","ProductInfo2","info");
        System.out.println(isTableExists("TestAllAtOnce3", "ProductInfo2"));
        /*putCell("TestAllAtOnce3","ProductInfo","0001","info","price","1500");
        putCell("TestAllAtOnce3","ProductInfo","0001","info","price","2300");
        //putCell("Test","ProductInfo","0001","info","price","3000");
        //putCell("Test","ProductInfo","0001","info","price","3050");
        //putCell("Test","ProductInfo","0001","info","price","4000");
        //测试读取数据
        Cell[] cells = getCells("TestAllAtOnce3","ProductInfo","0001","info","price");

        if(cells != null){
            for(Cell cell : cells){
                String value = new String(CellUtil.cloneValue(cell));
                System.out.println(value);
            }

        }

        //测试判断表格是否存在
        //System.out.println(isTableExists("FEI","CLASS"));
        //测试创建表格
        //createTable("Test","ProductInfo","info");
        //System.out.println(isTableExists("Test","ProductInfo"));
        //测试修改版本
        //modifyTableVersion("Test","ProductInfo","info",6);
        //测试删除表格
        //deleteTable("Test","ProductInfo");*/

        // 其他代码
        System.out.println("其他代码");
        // 关闭连接
        HBaseConnection.closeConnection();

    }


}
