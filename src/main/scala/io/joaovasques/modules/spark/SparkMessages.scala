package play.modules.io.joaovasques.playspark.spark

import org.apache.spark.SparkConf
import play.modules.io.joaovasques.playspark.api.SparkJob

object SparkMessages {

  trait SparkContextMessage
  case class GetContexts() extends SparkContextMessage
  case class SaveContext(conf: SparkConf) extends SparkContextMessage
  case class StartContext(contextId: String) extends SparkContextMessage
  case class StopContext() extends SparkContextMessage
  case class DeleteContext(contextId: String) extends SparkContextMessage

  trait SparkJobMessage
  case class StartSparkJob(job: SparkJob, contextId: String, async: Boolean) extends SparkJobMessage
  case class JobStarted(jobId: String) extends SparkJobMessage

  trait SparkJobResult
  case class JobCompleted(jobId: String, result: Any) extends SparkJobResult
  case class JobFailed(jobId: String, error: Throwable, time: Long) extends SparkJobResult
}

