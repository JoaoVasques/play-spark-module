package play.module.io.joaovasques.playspark.spark

import org.apache.spark.SparkConf
import play.module.io.joaovasques.playspark.api.SparkJob

object SparkMessages {

  trait SparkContextMessage

  case class GetContexts() extends SparkContextMessage
  case class SaveContext(conf: SparkConf) extends SparkContextMessage
  case class StartContext(contextId: String) extends SparkContextMessage
  case class StopContext() extends SparkContextMessage
  case class DeleteContext(contextId: String) extends SparkContextMessage

  trait SparkJobMessage
  case class StartSparkJob(job: SparkJob, contextId: String) extends SparkJobMessage
}

