package play.modules.io.joaovasques.playspark.spark

object SparkExceptions {

  // Context Exceptions
  case class SparkContextNotFoundException() extends Exception("Spark Context not found")
  case class SparkContextNotRunningException() extends Exception("No Spark Context running")
  case class SparkContextUnableToStartException() extends Exception("Couldn't start Spark Context")
}

