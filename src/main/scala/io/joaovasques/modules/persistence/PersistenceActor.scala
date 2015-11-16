package play.module.io.joaovasques.playspark.persistence

import akka.actor.{Actor, ActorRef, Props}
import java.util.UUID
import play.module.io.joaovasques.playspark.akkaguice.NamedActor
import com.google.inject.{BindingAnnotation, Inject}
import com.google.inject.name.Named
import play.module.io.joaovasques.playspark.persistence.PersistenceMessages._
import com.mongodb.casbah.Imports._

object PersistenceActor extends NamedActor {
  override final val name = "PersistenceActor"
}

class PersistenceActor extends Actor {

  private val (mongoClient, database) = {
    val uri = MongoClientURI("mongodb://localhost:27017/")
    (MongoClient(uri), MongoClient(uri)("playspark"))
  }

  override def postStop(): Unit = {
    mongoClient.close()
    super.postStop()
  }

  def receive = {
    case  request @ (Find(_,_,_,_) | Insert(_,_) | Update(_,_,_,_,_) | Delete(_,_,_)) => {
      val workerId = UUID.randomUUID().toString()
      context.actorOf(PersistenceWorker.props(database), name = s"Persistence-Worker-${workerId}") forward request
    }
  }
}

