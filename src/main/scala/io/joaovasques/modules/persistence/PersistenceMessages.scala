package play.modules.io.joaovasques.playspark.persistence

import play.api.libs.json._

object PersistenceMessages {

  case class Insert(element: JsValue, collection: String)
  case class Find(key: String, value: String, collection: String, single: Boolean = true)
  case class Update(key: String, value: String, fieldKey: String, newValue: String, collection: String)
  case class Delete(key: String, value: String, collection: String)


  // case class GetJob(jobId: String)
  // case class GetJobs(ids: List[String], option: Option[Int] = None)
  // case class PersistJob(job: Any, limit: Option[Int] = None)
}

