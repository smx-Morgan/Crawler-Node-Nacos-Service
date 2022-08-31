package com.crawler.hbase;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.ColumnValueFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class HBaseDML {
    public static Connection connection = HBaseConnection.conn;

    /**
     * 插入数据
     *
     * @param namespace    命名空间名称
     * @param tableName    表格名称
     * @param rowKey       主键
     * @param columnFamily 列族名称
     * @param columnName   列名
     * @param value        值
     */
    public static void putCell(String namespace, String tableName, String rowKey, String columnFamily, String columnName, String value) throws IOException {

        Table table = connection.getTable(TableName.valueOf(namespace, tableName));

        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName), Bytes.toBytes(value));
        try {
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        table.close();
    }

    /**
     * 读取数据
     *
     * @param namespace    命名空间名称
     * @param tableName    表格名称
     * @param rowKey       主键
     * @param columnFamily 列族名
     * @param columnName   列名
     * @return 读取到的Cell数组(一列数据的所有版本)
     */
    public static Cell[] getCells(String namespace, String tableName, String rowKey, String columnFamily, String columnName) throws IOException {

        Table table = connection.getTable(TableName.valueOf(namespace, tableName));

        Get get = new Get(Bytes.toBytes(rowKey));

        get.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName));
        get.readAllVersions();

        Result result = null;
        try {
            result = table.get(get);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Cell[] cells = result.rawCells();

        table.close();

        return cells;


    }

    /**
     * 扫描全表
     *
     * @param namespace 命名空间名称
     * @param tableName 表格名称
     * @return 行组成的数组
     */

    public static ResultScanner scanTable(String namespace, String tableName) throws IOException {
        Table table = connection.getTable(TableName.valueOf(namespace, tableName));
        Scan scan = new Scan();

        ResultScanner scanner = null;
        try {
            scanner = table.getScanner(scan);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        table.close();
        return scanner;
    }

    /**
     * 根据 rowKey 扫描数据(无法只扫描一行且只读取最新版本)
     *
     * @param namespace 命名空间名称
     * @param tableName 表格名称
     * @param startRow  开始的rowKey(包含)
     * @param stopRow   结束的rowKey(不包含)
     * @return ResultScanner记录多行数据(cell一列, result一行多列, return 多行)
     */

    public static ResultScanner scanRows(String namespace, String tableName, String startRow, String stopRow) throws IOException {
        Table table = connection.getTable(TableName.valueOf(namespace, tableName));
        Scan scan = new Scan();
        scan.withStartRow(Bytes.toBytes(startRow));
        scan.withStopRow(Bytes.toBytes(stopRow));

        ResultScanner scanner = null;
        try {
            scanner = table.getScanner(scan);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        table.close();

        return scanner;

    }

    /**
     * 打印读取的数据
     *
     * @param cells 读取到的一列数据
     */
    public static void println(Cell[] cells) {
        for (Cell cell : cells) {
            System.out.println(new String(CellUtil.cloneRow(cell)) + " - " + new String(CellUtil.cloneQualifier(cell)) + " - " + new String(CellUtil.cloneValue(cell)));
        }

    }

    /**
     * 打印扫描到的数据
     *
     * @param scanner 扫描到的多行数据
     */
    public static void println(ResultScanner scanner) {

        for (Result result : scanner) {
            Cell[] cells = result.rawCells();
            for (Cell cell : cells) {
                System.out.println(new String(CellUtil.cloneRow(cell)) + "-" +
                        new String(CellUtil.cloneFamily(cell)) + "-" +
                        new String(CellUtil.cloneQualifier(cell)) + "-" +
                        new String(CellUtil.cloneValue(cell)) + "\t");

            }
            System.out.println();
        }
    }

    /**
     * 单列过滤的扫描(若存在只保留一列）
     *
     * @param namespace    命名空间名称
     * @param tableName    表格名称
     * @param startRow     开始的rowKey
     * @param stopRow      结束的rowKey
     * @param columnFamily 列族名称
     * @param columnName   列名
     * @param value        值
     * @return ResultScanner 一列的值
     */
    public static ResultScanner columnFilterScan(String namespace, String tableName, String startRow, String stopRow, String columnFamily, String columnName, String value) throws IOException {
        Table table = connection.getTable(TableName.valueOf(namespace, tableName));

        Scan scan = new Scan();
        scan.withStartRow(Bytes.toBytes(startRow));
        scan.withStopRow(Bytes.toBytes(stopRow));

        // 过滤
        FilterList filterList = new FilterList();
        ColumnValueFilter columnValueFilter = new ColumnValueFilter(
                Bytes.toBytes(columnFamily),
                Bytes.toBytes(columnName),
                CompareOperator.EQUAL,
                Bytes.toBytes(value)
        );
        filterList.addFilter(columnValueFilter);
        scan.setFilter(filterList);

        ResultScanner scanner = null;
        try {
            scanner = table.getScanner(scan);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        table.close();

        return scanner;

    }

    /**
     * 整行过滤扫描(若存在保留一行且保留没有此列值的行)
     *
     * @param namespace    命名空间名称
     * @param tableName    表格名称
     * @param startRow     开始的rowKey
     * @param stopRow      结束的rowKey
     * @param columnFamily 列族名称
     * @param columnName   列名
     * @param value        值
     * @return 一行的数据
     */

    public static ResultScanner RowFilterScan(String namespace, String tableName, String startRow, String stopRow, String columnFamily, String columnName, String value) throws IOException {

        Table table = connection.getTable(TableName.valueOf(namespace, tableName));

        Scan scan = new Scan();
        scan.withStartRow(Bytes.toBytes(startRow));
        scan.withStopRow(Bytes.toBytes(stopRow));

        // 过滤
        // 结果会同时保留没有当前列数据的行
        FilterList filterList = new FilterList();
        SingleColumnValueFilter singleColumnValueFilter = new SingleColumnValueFilter(
                Bytes.toBytes(columnFamily),
                Bytes.toBytes(columnName),
                CompareOperator.EQUAL,
                Bytes.toBytes(value)
        );
        filterList.addFilter(singleColumnValueFilter);
        scan.setFilter(filterList);

        ResultScanner scanner = null;
        try {
            scanner = table.getScanner(scan);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        table.close();

        return scanner;

    }

    /**
     * 删除一行中的一列数据(所有版本)
     *
     * @param namespace    命名空间
     * @param tableName    表格名称
     * @param rowKey       行键
     * @param columnFamily 列族名称
     * @param columnName   列名
     */
    public static void deleteColumn(String namespace, String tableName, String rowKey, String columnFamily, String columnName) throws IOException {
        Table table = connection.getTable(TableName.valueOf(namespace, tableName));
        Delete delete = new Delete(Bytes.toBytes(rowKey));

        // 删除所有版本
        delete.addColumns(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName));

        try {
            table.delete(delete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        table.close();
    }


    public static void main(String[] args) throws IOException {

        // 测试读取数据
        // Cell[] cells = getCells("Test","ProductInfo","0001","info","price");
        // println(cells);
        // deleteColumn("Test","ProductInfo","0001","info","price");
        //
        /*createTable("Test","tableWithTTL","info");
        putCell("Test","tableWithTTL","0001","info","price","1000");*/
        // putCell("Test","tableWithTTL","0001","info","number","1500");
        //HBaseDDL.deleteTable("JDProducts", "Phones");
        //System.out.println(scanTable("JDProducts","Phones"));
        ResultScanner scanner = scanTable("JDProducts", "Phones");
        println(scanner);
        // System.out.println(isTableExists("JDProducts","Phones"));
       /* System.out.println("15:10");//3min后删除09插入一条||12插入一条
        Cell[] cellsAfter1 = getCells("Test","tableWithTTL","0001","info","price");
        println(cellsAfter1);
        Cell[] cellsAfter2 = getCells("Test","tableWithTTL","0001","info","number");
        println(cellsAfter2);*/


        // 其他代码
        System.out.println("其他代码");
    }
}
