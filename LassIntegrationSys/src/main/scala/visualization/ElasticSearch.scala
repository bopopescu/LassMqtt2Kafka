package visualization

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.slf4j.LoggerFactory
import parser.ElasticConfigParser
import dispatch._, Defaults._
import org.json4s.native.Json
import org.json4s.DefaultFormats
import com.github.nscala_time.time.Imports._

/**
 * Created by luoruhong on 2015/11/10.
 */
class ElasticSearch {
  val logger = LoggerFactory.getLogger(classOf[ElasticSearch])
  val elasticConfigs = ElasticConfigParser.readConfigs

  def appendEsConfigs(conf: SparkConf): ElasticSearch = {
    elasticConfigs.get.foreach { pair =>
      conf.set(pair._1, pair._2.toString)
    }
    this
  }

  def saveToEs(allParams: Array[Map[String, String]]) = {
    allParams.foreach { params =>
      val doc = Json(DefaultFormats).write(params)
      val esPath = host("master1", 9200) / "spark" / "basic" / DateTime.now.millis.toString
      val esReq = esPath.PUT << doc
      val response = Http(esReq OK as.String)
      println(response())
    }
  }

}