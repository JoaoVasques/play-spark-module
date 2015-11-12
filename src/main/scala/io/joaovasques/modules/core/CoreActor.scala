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
import play.module.io.joaovasques.playspark.spark.SparkActor
import play.module.io.joaovasques.playspark.spark.SparkMessages._

object CoreActor extends NamedActor {
  override final val name = "CoreActor"
}

class CoreActor @Inject()(
  @Named(JobExecutionActor.name) jobExecutionActor: ActorRef,
  @Named(PersistenceActor.name) persistenceActor: ActorRef,
  @Named(StatsActor.name) statsActor: ActorRef,
  @Named(SparkActor.name) sparkActor: ActorRef
) extends Actor {

  private def handleJobExecutionRequest: Receive = {
    case m @ StartJob(_) => {
      jobExecutionActor ! m
      sender ! new JobStarted()
    }
  }

  private def handlePersistenceRequest: Receive = {
    case m @ (Insert(_,_) | Update(_,_,_,_,_) | Delete(_,_,_)) => persistenceActor ! m
    case m @ Find(_,_,_,_) => persistenceActor forward m
  }

  private def handleStatsRequest: Receive = {
    case m @ (GetJobsInExecution(_) | GetCompletedJobs(_) | GetFailedJobs(_)) => statsActor forward m
  }

  private def handleSparkRequest: Receive = {
    case m @ GetContext(_) => sparkActor forward m
    case m @ SaveContext(_) => sparkActor ! m
  }

  private def unhandled: Receive = {
    case m @ _ => sender ! akka.actor.Status.Failure(new MatchError(m))
  }

  def receive = handleJobExecutionRequest orElse handlePersistenceRequest orElse handleStatsRequest orElse handleSparkRequest orElse  unhandled
  
}

