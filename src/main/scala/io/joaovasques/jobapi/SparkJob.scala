package play.modules.io.joaovasques.playspark.api

import org.apache.spark.SparkContext

trait SparkJob extends Serializable{

  def runJob(context: SparkContext): Any
}

