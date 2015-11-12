package play.module.io.joaovasques.playspark.core

import akka.actor.{Actor, ActorRef}
import play.module.io.joaovasques.playspark.akkaguice.NamedActor
import com.google.inject.{BindingAnnotation, Inject}
import com.google.inject.name.Named
import play.module.io.joaovasques.playspark.execution.JobExecutionMessages._
import play.module.io.joaovasques.playspark.persistence.PersistenceMessages._
import play.module.io.joaovasques.playspark.stats.StatsMessages._
import play.module.io.joaovasques.playspark.execution.{JobExecutionActor}
import play.module.io.joaovasques.playspark.persistence.{PersistenceActor}
import play.module.io.joaovasques.playspark.stats.StatsActor

object CoreActor extends NamedActor {
  override final val name = "CoreActor"
}

class CoreActor @Inject()(
  @Named(JobExecutionActor.name) jobExecutionActor: ActorRef,
  @Named(PersistenceActor.name) persistenceActor: ActorRef,
  @Named(StatsActor.name) statsActor: ActorRef
) extends Actor {

  private def handleJobExecutionRequest: Receive = {
    case m @ StartJob(_) => jobExecutionActor ! m
  }

  private def handlePersistenceRequest: Receive = {
    case m @  PersistJob(_, _) => persistenceActor ! m
    case m @ (GetJob(_) | GetJobs(_, _)) => persistenceActor forward m
  }

  private def handleStatsRequest: Receive = {
    case m @ (GetJobsInExecution(_) | GetCompletedJobs(_) | GetFailedJobs(_)) => statsActor forward m
  }

  private def unhandled: Receive = {
    case m @ _ => sender ! akka.actor.Status.Failure(new MatchError(m))
  }

  def receive = handleJobExecutionRequest orElse handlePersistenceRequest orElse handleStatsRequest orElse unhandled
  
}

