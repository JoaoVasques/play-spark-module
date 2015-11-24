package play.modules.io.joaovasques.playspark.spark

import org.apache.spark.SparkConf
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import scala.language.implicitConversions

object SparkImplicits {

  private final val SparkProperties = List(
    "spark.driver.host",
    "spark.driver.port",
    "spark.driver.cores",
    "spark.driver.maxResultSize",
    "spark.driver.memory",
    "spark.executor.memory",
    "spark.extraListeners",
    "spark.local.dir",
    "spark.logConf",
    "spark.akka.threads",
    "spark.executor.id",
    "spark.externalBlockStore.folderName",
    "spark.fileserver.uri"
  )

  implicit def sparkConfToJson(conf: SparkConf): JsValue = {
    val splitted = conf.toDebugString.split("\n")
    Json.toJson(splitted.foldLeft(Map.empty[String,String]){(map,currentConf) => 
      val keyValueConf = currentConf.split("=")
      map + (keyValueConf.head.replace(".","_") -> keyValueConf.last.replace(".","_"))
    })
  }

  implicit def fromJsonToSparkConf(json: JsValue): SparkConf = {
    def getValue(key: String): String = {
      (json \ key).as[List[String]].head.replace("_", ".")
    }

    def keyExists(key: String): Boolean = {
      json.as[JsObject].keys.contains(key)
    }
    val conf = new SparkConf()

    /** Application Properties **/
    // set master
    conf.setMaster(getValue("spark_master"))

    // set app name
    conf.setAppName(getValue("spark_app_name"))

    // set spark home
    if(keyExists("spark_home")) {
      conf.setSparkHome(getValue("spark_home"))
    }

    // other properties
    SparkProperties.foreach{property =>
      if(keyExists(property.replace(".", "_"))) {
        conf.set(property, getValue(property.replace(".", "_")))
      }
    }
    conf
  }
}

