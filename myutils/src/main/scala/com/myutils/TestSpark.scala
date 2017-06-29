package com.myutils

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import com.alibaba.fastjson.JSON
import scala.util.Try

class TestSpark {
  
}

object TestSpark{
  
  def initSpark: SparkContext = {
		val conf = new SparkConf()
        conf.setAppName("WordCount") //
            .setMaster("local")

        val sc = new SparkContext(conf)
		sc
	}
  
  def main(args: Array[String]): Unit = {
    val sc = initSpark
//    wordCount(sc)
    testJsonObject(sc)
  }
  
  def testJsonObject(sc: SparkContext){
    val rdd = sc.textFile("C:\\Users/july/Desktop/logs/hl/hllaunch2017010514985457860009391752.txt")
    val jsonRdd =rdd.map(x => {
        Try(JSON.parseObject(x))
      }).filter(_.isSuccess).map(_.get)
      for(x<-jsonRdd.collect()){
        println(x.getString("event"))
      }
  }
  
  def testSpark(sc: SparkContext){
    val rdd = sc.parallelize(List((1,2),(1,3),(3,4),(3,6)))
    rdd.map(x => (x._1,x._2+1))
    for(x<-rdd.collect()){
      println(x)
    }
    print("====================================")
    rdd.reduce((x,y)=>x)
    
    for(x<-rdd.collect()){
      println(x)
    }
    
    sc.stop()
  }
  
  def wordCount(sc: SparkContext){
        val filePath = Thread.currentThread().getContextClassLoader.getResource("word.txt").toString()
        //获取文件内容
        val lines = sc.textFile(filePath, 1)
        //分割单词
        val words = lines.flatMap(lines => lines.split(" "))
        val pairs = words.map(word => (word,1))
        pairs.foreach(e => print(e))
        println()
        val testResult = pairs.reduceByKey((x,y) => x)
        println("=============testResult===========")
        testResult.foreach(e => print(e))
        println()
         val testResult2 = pairs.reduceByKey((_, _) => 1)
        println("=============testResult2===========")
        testResult2.foreach(e => print(e))
        println()
        val testResult3 = pairs.map(x =>{
          val y = x._2+1
          (x._1,y)
        })
        println("=============testResult3===========")
        testResult3.foreach(e => print(e))
        println()
        
        val result = pairs.reduceByKey((word, acc) => word + acc)
        result.foreach(e => print(e))
        //sort by count DESC
        val sorted = result.sortBy(e => {e._2}, false, 1)
        //val mapped = sorted.map(e => (e._2,e._1))
        sorted.foreach(e => println("【" + e._1 + "】出现了" + e._2 + "次"))
        sc.stop()
	}
	
	def findTextInfo(sc: SparkContext){
		 val filePath = Thread.currentThread().getContextClassLoader.getResource("word.txt").toString()
        //获取文件内容
        val lines = sc.textFile(filePath, 1)
        //筛选警告信息
        val warnInfo = lines.filter(line => line.contains("WARN"))
        println("警告信息数目： "+warnInfo.count())
        //取前10条数据
        warnInfo.take(10).foreach(println(_))
	}
	
  
}