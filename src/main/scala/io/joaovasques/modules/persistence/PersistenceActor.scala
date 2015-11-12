package play.module.io.joaovasques.playspark.persistence

import akka.actor.{Actor, ActorRef}
import play.module.io.joaovasques.playspark.akkaguice.NamedActor
import com.google.inject.{BindingAnnotation, Inject}
import com.google.inject.name.Named

object PersistenceActor extends NamedActor {
  override final val name = "PersistenceActor"
}

class PersistenceActor extends Actor {

  def receive = {
    case _ => println("hello from PersistenceActor")
  }
}

