package com.myutils

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import scala.util.Try
import com.alibaba.fastjson.JSON
import java.net.URL
import org.apache.commons.io.IOUtils
import java.nio.charset.Charset

class HdfsDataNum {
  
}

object HdfsDataNum{
  
  val fingerId = "0bd5152d38d213f7475f8f0dbea942cc"
  
  def initSpark: SparkContext = {
		val conf = new SparkConf()
        conf.setAppName("HdfsDataNum") //
            .setMaster("local[3]")

        val sc = new SparkContext(conf)
		sc
	}
  
  def main(args: Array[String]): Unit = {
    
    val hdfsPath = "hdfs://bigdata2:8020/flume/events/17/08/18/*.gz"
    
    val sc = initSpark
    
   
    
    
    val data = sc.textFile(hdfsPath).coalesce(100, true)
    .map(x=>Try(JSON.parseObject(x))).filter(_.isSuccess).map(_.get)
    val dataNum = data.filter(x=> fingerId.equals(x.get("fingerId")))
    
    println("dataNum: "+dataNum.count())
    
    
  }
  
}