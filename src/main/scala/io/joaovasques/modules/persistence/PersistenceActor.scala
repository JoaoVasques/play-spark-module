package play.module.io.joaovasques.playspark.persistence

import akka.actor.{Actor, ActorRef, Props}
import java.util.UUID
import play.module.io.joaovasques.playspark.akkaguice.NamedActor
import com.google.inject.{BindingAnnotation, Inject}
import com.google.inject.name.Named
import play.module.io.joaovasques.playspark.persistence.PersistenceMessages._

object PersistenceActor extends NamedActor {
  override final val name = "PersistenceActor"
}

class PersistenceActor extends Actor {

  def receive = {
    case request @ (PersistJob(_, _) | GetJob(_) | GetJobs(_, _)) => {
      val workerId = UUID.randomUUID().toString()
      context.actorOf(Props[PersistenceWorker], name = s"Persistence-Worker-${workerId}") ! request
    }
  }
}

