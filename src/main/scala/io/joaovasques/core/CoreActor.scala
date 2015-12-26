package play.modules.io.joaovasques.playspark.core

import akka.actor.{Actor, ActorRef}
import play.modules.io.joaovasques.playspark.akkaguice.NamedActor
import com.google.inject.{BindingAnnotation, Inject}
import com.google.inject.name.Named
import play.modules.io.joaovasques.playspark.persistence.PersistenceMessages._
import play.modules.io.joaovasques.playspark.stats.StatsMessages._
import play.modules.io.joaovasques.playspark.persistence.{MyPersistenceActor}
import play.modules.io.joaovasques.playspark.stats.StatsActor
import play.modules.io.joaovasques.playspark.spark.SparkActor
import play.modules.io.joaovasques.playspark.spark.SparkMessages._

object CoreActor extends NamedActor {
  override final val name = "CoreActor"
}

class CoreActor @Inject()(
  @Named(MyPersistenceActor.name) persistenceActor: ActorRef,
  @Named(StatsActor.name) statsActor: ActorRef,
  @Named(SparkActor.name) sparkActor: ActorRef
) extends Actor {

  private def handlePersistenceRequest: Receive = {
    case m @ (Find(_,_,_,_) | Insert(_,_) | Update(_,_,_,_,_) | Delete(_,_,_)) => persistenceActor forward m
  }

  private def handleStatsRequest: Receive = {
    case m @ (GetJobsInExecution(_) | GetCompletedJobs(_) | GetFailedJobs(_)) => statsActor forward m
  }

  private def handleSparkRequest: Receive = {
    case m @ (GetContexts() | SaveContext(_) | DeleteContext(_) | StopContext() | StartSparkJob(_,_,_)) => {
      sparkActor forward m
    }
  }

  private def unhandled: Receive = {
    case m @ _ => sender ! akka.actor.Status.Failure(new MatchError(m))
  }

  def receive = handlePersistenceRequest orElse handleStatsRequest orElse handleSparkRequest orElse unhandled
  
}

