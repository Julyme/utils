package com.myutils

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

class YarnJobTestMain {
   def initSpark: SparkContext = {
		val conf = new SparkConf()
        conf.setAppName("Job数量测试") //

        val sc = new SparkContext(conf)
		sc
	}
}

object YarnJobTestMain{
  
  def main(args: Array[String]): Unit = {
    val job = new YarnJobTestMain
     val sc = job.initSpark
     sc.stop()
  }
}