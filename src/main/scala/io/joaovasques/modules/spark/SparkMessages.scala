package play.module.io.joaovasques.playspark.spark

import org.apache.spark.SparkContext

object SparkMessages {

  case class GetContext(contextId: String)
  case class GetContexts()
  case class SaveContext(context: SparkContext)

  case class StopContext(contextId: String)
  case class RestartContext(contextId: String)
  case class DeleteContext(contextId: String)
}

