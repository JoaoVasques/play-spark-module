package play.modules.io.joaovasques.playspark.stats

object StatsMessages {

  case class GetJobsInExecution(limit: Option[Int] = None)
  case class GetCompletedJobs(limit: Option[Int] = None)
  case class GetFailedJobs(limit: Option[Int] = None)

}

