package play.module.io.joaovasques.playspark.persistence

import akka.actor.Props
import akka.actor.{Actor}
import play.api.libs.json.JsObject
import play.module.io.joaovasques.playspark.persistence.PersistenceMessages._
import com.mongodb.casbah.Imports._

private[persistence] sealed class PersistenceWorker(db: MongoDB) extends Actor {

  private def handleInsert: Receive = {
    case req @ Insert(el, collection) => {
      val query = DBObject(el.as[JsObject].fields.toList)
      println("INSERT QUERT: " + query.toMap().toString())
      val res = db(collection).insert(query)
      println(res)
    }
  }

  private def handleFind: Receive = {
    case req @ Find(_,_,_,limitOpt) => {
      println("GetJob")
      sender ! "TODO"
    }
  }

  private def handleUpdate: Receive = {
    case req @ Update(_,_,_,_,_) => {
      println("GetJobs")
      sender ! List("TODO")
    }
  }

  private def handleDelete: Receive = {
    case req @ Delete(_,_,_) => {
      println("GetJobs")
      sender ! List("TODO")
    }
  }

  private def unhandled: Receive = {
    case _ => println("unhandled persistence worker")
  }

  def receive = handleInsert orElse handleFind orElse handleUpdate orElse handleDelete orElse unhandled
}

object PersistenceWorker {

  def props(client: MongoDB): Props = Props(new PersistenceWorker(client))
}

