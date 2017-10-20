package com.myutils

import java.io.File
import com.myutils.files.FileUtils
import java.util.List
import java.util.ArrayList
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.elasticsearch.spark._
import scala.collection.Seq
import com.myutils.kafka.KafkaUtils

class DataToEs {

}

object DataToEs {

  def getFilePaths(path:String, list: List[String]):List[String]={
		val f = new File(path)
		if (f.isDirectory()){
			for (childFile <- f.listFiles()) {
				if(childFile.isDirectory()){
					getFilePaths(childFile.getPath(), list)
				}else{
					list.add(childFile.getPath())
				}
			}
		}else{
			list.add(f.getPath())
		}
		list
	}
 
  
  def dataSentToEs(list:List[String], sc: SparkContext): Unit = {
    
    var rdd: RDD[String] = null
    
    for(path <- list.toArray()){
      val file = sc.textFile(path.toString())
      if(Option(rdd).isDefined){
        rdd = rdd.union(file)
      }else{
        rdd = file
      }
    }
    
    val nRdd = rdd.map(x =>
      {
        KafkaUtils.sendData("ghwdata", x)
      })
      
    println(nRdd.count())
    
  }
  
  def main(args: Array[String]): Unit = {
		  var list: List[String] =new ArrayList()
		  
		  getFilePaths("F:\\data\\生产环境测试数据\\2017-04\\01", list)
		  
		  val  sc = initSpark 
		  
		  dataSentToEs(list, sc)
  }

 def initSpark: SparkContext = {
		val conf = new SparkConf()
        conf.setAppName("dataSentToEs") //
            .setMaster("local[3]")

        val sc = new SparkContext(conf)
		sc
	}


}