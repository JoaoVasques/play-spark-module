package play.module.io.joaovasques.playspark.persistence

object PersistenceMessages {

  case class GetJob(jobId: String)
  case class GetJobs(ids: List[String], option: Option[Int] = None)
  case class PersistJob(job: Any, limit: Option[Int] = None)
}

