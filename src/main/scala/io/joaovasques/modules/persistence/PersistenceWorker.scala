package play.module.io.joaovasques.playspark.persistence

import akka.actor.{Actor}
import play.module.io.joaovasques.playspark.persistence.PersistenceMessages.GetJob
import play.module.io.joaovasques.playspark.persistence.PersistenceMessages.GetJobs
import play.module.io.joaovasques.playspark.persistence.PersistenceMessages.PersistJob

private[persistence] sealed class PersistenceWorker extends Actor {

  private def handlePersistJob: Receive = {
    case req @ PersistJob(_, _) => {
      println("PersistJob")
    }
  }

  private def handleGetJob: Receive = {
    case req @ GetJob(_) => {
      println("GetJob")
      sender ! "TODO"
    }
  }

  private def handleGetJobs: Receive = {
    case req @ GetJobs(_, _) => {
      println("GetJobs")
      sender ! List("TODO")
    }
  }

  private def unhandled: Receive = {
    case _ => println("unhandled persistence worker")
  }

  def receive = handleGetJob orElse handleGetJob orElse handleGetJob orElse unhandled
}

