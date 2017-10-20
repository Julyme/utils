package com.myutils.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myutils.properties.PropertiesUtils;

public class KafkaUtils {

    private static final Logger logger = LoggerFactory.getLogger(KafkaUtils.class);
    
    private String data = "{\"_adsetName\": \"hladsetName5506859\", \"_city\": \"Shanghai\", \"_country\": \"CN\", \"_ip\": \"172.16.50.170\", \"_source\": \"sdk_bk\", \"_time\": 1501747837000, \"_version\": \"3.6.1\", \"adId\": \"hladId7868441\", \"adsetId\": \"hladsetId5815263\", \"agency\": \"\", \"appId\": \"e01a2321d88811e6b6ad8aaa721891f8\", \"appTz\": \"Etc/GMT\", \"campaignId\": \"\", \"channelId\": \"\", \"context\": {\"device\": \"Huawei\", \"deviceBrand\": \"Huawei\", \"deviceType\": \"Huawei\", \"isFirstEnter\": \"1\", \"level\": \"0\", \"model\": \"Huawei\", \"network\": \"WIFI\", \"op\": \"CMCC\", \"os\": \"android\", \"osVersion\": \"3.6.1\", \"product\": \"Y560-L01\", \"resolution\": \"854*480\", \"tz\": \"+0800\"}, \"deviceId\": \"Huawei\", \"event\": \"ghw_startup\", \"fingerId\": \"475f8f0dbea942cc0bd5152d38d213f7\", \"gameUserId\": \"-1\", \"installCount\": \"1\", \"launchTime\": 1501747837000, \"platform\": \"android\", \"reinstallContext\": {\"adId\": \"hladId7868441\", \"adsetId\": \"hladsetId5815263\", \"agency\": \"\", \"campaignId\": \"\", \"channelId\": \"\", \"city\": \"Shanghai\", \"country\": \"CN\", \"ip\": \"172.16.50.170\"}, \"reinstallTimeDiff\": 0, \"relaunchTime\": 1501747837000, \"sdkVersion\": \"3.6.1\", \"serverId\": \"165\", \"sessionId\": \"1501747837000\", \"timeDiff\": 0, \"userId\": \"-1\", \"value\": \"1.211\"}";
   
    
    private static Properties properties = PropertiesUtils.loadProperties("kafka-producer.properties");
    private static KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
    
    private static int count = 0;
    
    private static Callback callback = new Callback() {
        // 异步请求返回，也是异步请求的一个标志
        @Override
        public void onCompletion(RecordMetadata metadata, Exception exception) {
            if (exception != null) {
                logger.error("Send Data Error:- " + exception.getMessage());
                System.out.println(++count);
            }
        }
    };
    
    public static void sendData(String topic, String data){
        producer.send(
                new ProducerRecord<String, String>(topic, data),callback); 
    }
    
    public static void main(String[] args) {
       KafkaUtils kafkaUtils = new KafkaUtils();
       long t= System.currentTimeMillis();
       for (int i = 1; i <= 1000*10000; i++) {
    	   kafkaUtils.sendData("ghwdata", kafkaUtils.data);
	}
//       System.out.println(kafkaUtils.properties);
       System.out.println("发送完毕:"+(System.currentTimeMillis() - t));
       
       try {
		Thread.sleep(600*1000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
    }
}
