package com.myutils.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.myutils.kafka.KafkaUtils;
import com.sun.tools.hat.internal.parser.ReadBuffer;

public class FileUtils {

	public static List<String> getFilePaths(String path, List<String> list){
		File f = new File(path);
		if (f.isDirectory()){
			for (File childFile : f.listFiles()) {
				if(childFile.isDirectory()){
					getFilePaths(childFile.getPath(), list);
				}else{
					list.add(childFile.getPath());
				}
			}
		}else{
			list.add(f.getPath());
		}
		return list;
	}
	
	
	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list=getFilePaths("F:\\data\\生产环境测试数据\\2017-04", list);
		int i = 0;
		
		for (String path : list) {
			try {
				BufferedReader read = new BufferedReader(
						new InputStreamReader(new FileInputStream(
								new File(path))));
				while(read.ready()){
					String s = read.readLine();
//					KafkaUtils.sendData("ghwdata", s);
					System.out.println(s);
					System.out.println(i++);
					break;
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
