package play.module.io.joaovasques.playspark.spark

import org.apache.spark.SparkConf
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import scala.language.implicitConversions

object SparkImplicits {

  implicit def sparkConfToJson(conf: SparkConf): JsValue = {
    val splitted = conf.toDebugString.split("\n")
    Json.toJson(splitted.foldLeft(Map.empty[String,String]){(map,currentConf) => 
      val keyValueConf = currentConf.split("=")
      map + (keyValueConf.head.replace(".","_") -> keyValueConf.last.replace(".","_"))
    })
  }
}

