/*package com.myutils.hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HBaseUtils {

	public static Configuration configuration;
	public static Connection connection;
	public static Admin admin;

	private static Logger log = LoggerFactory.getLogger(HBaseUtils.class);

	public static void main(String[] args) throws IOException {
		init();
		// createTable("t2",new String[]{"cf1","cf2"});
		// insterRow("t2", "rw1", "cf1", "q1", "val1");
		// getData("t2", "rw1", "cf1", "q1");
		// scanData("t2", "rw1", "rw2");
		// deleRow("t2","rw1","cf1","q1");
		// deleteTable("t2");
		//createTable("chassis", new String[] { "chassis", "status", "info" });
		// insterRow("c_test", "1", "chassis", "c", "我是地盘数据");
		// insterRow("c_test", "1", "status", "s", "我是状态数据");
		// insterRow("c_test", "1", "info", "i", "我是信息数据");
		 getData("c_test", "1", null, null);
		 close();
	}

	// 初始化链接
	public static void init() {

		configuration = HBaseConfiguration.create();
		// configuration.set("hbase.zookeeper.quorum","10.10.3.181,10.10.3.182,10.10.3.183");
		// configuration.set("hbase.zookeeper.property.clientPort","2181");
		// configuration.set("zookeeper.znode.parent","/hbase");
		configuration.set("hbase.zookeeper.quorum", "172.168.56.11");
		configuration.set("hbase.zookeeper.property.clientPort", "2181");
		if (connection == null || connection.isClosed()) {
			try {
				connection = ConnectionFactory.createConnection(configuration);
				admin = connection.getAdmin();
				log.info("连接初始化成功。");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	// 关闭连接
	public static void close() {
		try {
			if (null != admin)
				admin.close();
			if (null != connection)
				connection.close();
			log.info("连接已关闭");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 建表
	public static void createTable(String tableNmae, String[] cols)
			throws IOException {

		TableName tableName = TableName.valueOf(tableNmae);

		if (admin.tableExists(tableName)) {
			log.info("该表已经存在");
		} else {
			HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
			for (String col : cols) {
				HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(col);
				hTableDescriptor.addFamily(hColumnDescriptor);
			}
			admin.createTable(hTableDescriptor);
			log.info(tableNmae + "创建成功。");
		}
	}

	// 删表
	public static void deleteTable(String tableName) throws IOException {
		TableName tn = TableName.valueOf(tableName);
		if (admin.tableExists(tn)) {
			admin.disableTable(tn);
			admin.deleteTable(tn);
		}
		log.info(tableName + "删除成功");
	}

	// 查看已有表
	public static void listTables() throws IOException {
		HTableDescriptor hTableDescriptors[] = admin.listTables();
		for (HTableDescriptor hTableDescriptor : hTableDescriptors) {
			System.out.println(hTableDescriptor.getNameAsString());
		}
	}

	// 插入数据
	public static void insterRow(String tableName, String rowkey,
			String colFamily, String col, String val) throws IOException {
		Table table = connection.getTable(TableName.valueOf(tableName));
		Put put = new Put(Bytes.toBytes(rowkey));
		put.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col),
				Bytes.toBytes(val));
		table.put(put);

		// 批量插入
		
		 * List<Put> putList = new ArrayList<Put>(); puts.add(put);
		 * table.put(putList);
		 
		log.info(tableName + "插入一条数据");
		table.close();
	}

	// 删除数据
	public static void deleRow(String tableName, String rowkey,
			String colFamily, String col) throws IOException {
		Table table = connection.getTable(TableName.valueOf(tableName));
		Delete delete = new Delete(Bytes.toBytes(rowkey));
		// 删除指定列族
		// delete.addFamily(Bytes.toBytes(colFamily));
		// 删除指定列
		// delete.addColumn(Bytes.toBytes(colFamily),Bytes.toBytes(col));
		table.delete(delete);
		// 批量删除
		
		 * List<Delete> deleteList = new ArrayList<Delete>();
		 * deleteList.add(delete); table.delete(deleteList);
		 
		log.info(tableName + "删除一条数据。");
		table.close();
	}

	// 根据rowkey查找数据
	public static void getData(String tableName, String rowkey,
			String colFamily, String col) throws IOException {
		Table table = connection.getTable(TableName.valueOf(tableName));
		Get get = new Get(Bytes.toBytes(rowkey));
		// 获取指定列族数据
		// get.addFamily(Bytes.toBytes(colFamily));
		// 获取指定列数据
		// get.addColumn(Bytes.toBytes(colFamily),Bytes.toBytes(col));
		Result result = table.get(get);

		showCell(result);
		table.close();
	}

	// 格式化输出
	public static void showCell(Result result) {
		Cell[] cells = result.rawCells();
		for (Cell cell : cells) {
			System.out.println("RowName:" + new String(CellUtil.cloneRow(cell))
					+ " ");
			System.out.println("Timetamp:" + cell.getTimestamp() + " ");
			System.out.println("column Family:"
					+ new String(CellUtil.cloneFamily(cell)) + " ");
			System.out.println("row Name:"
					+ new String(CellUtil.cloneQualifier(cell)) + " ");
			System.out.println("value:" + new String(CellUtil.cloneValue(cell))
					+ " ");
		}
	}

	// 批量查找数据
	public static void scanData(String tableName, String startRow,
			String stopRow) throws IOException {
		Table table = connection.getTable(TableName.valueOf(tableName));
		Scan scan = new Scan();
		// scan.setStartRow(Bytes.toBytes(startRow));
		// scan.setStopRow(Bytes.toBytes(stopRow));
		ResultScanner resultScanner = table.getScanner(scan);
		for (Result result : resultScanner) {
			showCell(result);
		}
		table.close();
	}
}
*/