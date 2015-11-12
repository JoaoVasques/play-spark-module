package play.module.io.joaovasques.playspark.execution

import akka.actor.{Actor, ActorRef}
import play.module.io.joaovasques.playspark.akkaguice.NamedActor
import com.google.inject.{BindingAnnotation, Inject}
import com.google.inject.name.Named

object JobExecutionActor extends NamedActor {
  override final val name = "JobExecutionActor"
}

class JobExecutionActor extends Actor {

  def receive = {
    case _ => println("hello from JobExecutionActor")
  }
}

