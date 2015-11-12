package play.module.io.joaovasques.playspark.core

import akka.actor.{Actor, ActorRef}
import play.module.io.joaovasques.playspark.akkaguice.NamedActor
import com.google.inject.{BindingAnnotation, Inject}
import com.google.inject.name.Named
import play.module.io.joaovasques.playspark.execution.{JobExecutionActor}
import play.module.io.joaovasques.playspark.persistence.{PersistenceActor}

object CoreActor extends NamedActor {
  override final val name = "CoreCompanion"
}

class CoreActor @Inject()(
  @Named(JobExecutionActor.name) jobExecutionActor: ActorRef,
  @Named(PersistenceActor.name) persistenceActor: ActorRef
) extends Actor {


  def receive = {
    case m => {
      println("core actor...")
      println(jobExecutionActor)
      jobExecutionActor forward m
    }
  }
}

