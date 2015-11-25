package play.modules.io.joaovasques.playspark.stats

import akka.actor.{Actor}
import play.modules.io.joaovasques.playspark.stats.StatsMessages._

private[stats] sealed class StatsWorker extends Actor {

  private def handleGetJobsInExecution: Receive = {
    case req @ GetJobsInExecution(_) => {
      println("GetJobsInExecution")
    }
  }

  private def handleGetCompletedJobs: Receive = {
    case req @ GetCompletedJobs(_) => {
      println("GetCompletedJobs")
      sender ! "TODO"
    }
  }

  private def handleGetFailedJobs: Receive = {
    case req @ GetFailedJobs(_) => {
      println("GetFailedJobs")
      sender ! List("TODO")
    }
  }

  private def unhandled: Receive = {
    case _ => println("unhandled stats worker")
  }

  def receive = handleGetJobsInExecution orElse handleGetCompletedJobs orElse handleGetFailedJobs orElse unhandled
}

