package com.myutils.test;

public class TestStr {

	public static void main(String[] args) {
		String str = "[\"29 Jun 2017 08:25:34,548 INFO  [pool-4-thread-1] (org.apache.commons.httpclient.HttpMethodDirector.executeWithRetry:439)  - I/O exception (org.apache.commons.httpclient.NoHttpResponseException) caught when processing request: The server bigdata4 failed12";
		System.out.println(str.length());
		
		String dataPath = "/jmonitor/Kafka-11295-172.16.100.53";
		String name = dataPath.substring(dataPath.lastIndexOf("/")+1);
		System.out.println(name);
		
	}
	
}
