package com.myutils.zookeeper;


import com.github.zkclient.IZkClient;
import com.github.zkclient.ZkClient;

public class TestZookeeper {
	private static IZkClient zkClient = null;
//	private static String nodePath = "/rmstore/ZKRMStateRoot/RMAppRoot/application_1499049246164_2131/appattempt_1499049246164_2131_000001";
	private static String nodePath = "/rmstore/ZKRMStateRoot/RMAppRoot/application_1499049246164_2131";
	public TestZookeeper() {
		zkClient = new ZkClient("bigdata3:2181,bigdata1:2181,bigdata2:2181");
	}
	
	
	public static void main(String[] args) {
		
		if(args.length != 4){
			System.out.println("请添加如下变量：path、appPrefix、begin、end");
			System.out.println("例如储目录为：/rmstore/ZKRMStateRoot/RMAppRoot/application_1499049246164_2131/");
			System.out.println("我要删除1到2131的数据");
			System.out.println("执行命令为: java -jar xx.jar /rmstore/ZKRMStateRoot/RMAppRoot/ application_1499049246164_ 1 2131");
			return;
		}
		String path = args[0];
		String appPrefix = args[1];
		Integer begin = Integer.parseInt(args[2]);
		Integer end = Integer.parseInt(args[3]);
		
		TestZookeeper zk = new TestZookeeper();
		zk.deleteJobHistoryData(path, appPrefix, begin, end);;
	}
	
	
	/**
	 * 例如储目录为：/rmstore/ZKRMStateRoot/RMAppRoot/application_1499049246164_2131/	</br>
	 * 我要删除1到2131的数据	</br>
	 * path:/rmstore/ZKRMStateRoot/RMAppRoot/	</br>
	 * appPrefix:application_1499049246164_	</br>
	 * begin:1	</br>
	 * end:2131	</br>
	 * 
	 * @param path 存储的根目录
	 * @param appPrefix app名字前缀（包括第二个斜杠）
	 * @param begin job名字最后那个数字
	 * @param end job名字最后那个数字
	 */
	public void deleteJobHistoryData(String path, String appPrefix,Integer begin, Integer end){
		IZkClient zkClient = new ZkClient("bigdata3:2181,bigdata1:2181,bigdata2:2181");
		for(int i=begin; i<=end; i++){
			String id = String.format("%04d",i);
			String deletePath = path+appPrefix+id;
			boolean b = zkClient.deleteRecursive(deletePath);
			if(b){
				System.out.println("删除成功===>目录："+deletePath);
			}else{
				System.out.println("删除失败===>目录："+deletePath);
			}
		}
		zkClient.close();
		System.out.println("job History数据清楚完毕！");
	}
	
	
}
