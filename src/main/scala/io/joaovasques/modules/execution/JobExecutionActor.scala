package play.module.io.joaovasques.playspark.execution

import akka.actor.{Actor, ActorRef, Props}
import play.module.io.joaovasques.playspark.akkaguice.NamedActor
import com.google.inject.{BindingAnnotation, Inject}
import com.google.inject.name.Named
import play.module.io.joaovasques.playspark.execution.JobExecutionMessages._

object JobExecutionActor extends NamedActor {
  override final val name = "JobExecutionActor"
}

class JobExecutionActor extends Actor {

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    context.children foreach {child =>
      context.unwatch(child)
      context.stop(child)
    }
    postStop()
  }

  def receive = {
    case job @ StartJob(_) => {
      context.actorOf(Props[JobWorker], name = s"JobWorker-${job.id}") ! job
    }
  }
}

