package play.modules.io.joaovasques.playspark.stats

import akka.actor.{Actor, ActorRef}
import play.modules.io.joaovasques.playspark.akkaguice.NamedActor
import com.google.inject.{BindingAnnotation, Inject}
import com.google.inject.name.Named

object StatsActor extends NamedActor {
  override final val name = "StatsActor"
}

class StatsActor extends Actor {

  def receive = {
    case _ => println("hello from StatsActor")
  }
}

