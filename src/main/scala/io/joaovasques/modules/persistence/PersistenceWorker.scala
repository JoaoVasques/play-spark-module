package play.module.io.joaovasques.playspark.persistence

import akka.actor.Props
import akka.actor.{Actor}
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.module.io.joaovasques.playspark.persistence.PersistenceMessages._
import com.mongodb.casbah.Imports._
import scala.util.Failure
import scala.util.Success

private[persistence] sealed class PersistenceWorker(db: MongoDB) extends Actor {

  private def handleInsert: Receive = {
    case req @ Insert(el, collection) => {
      val query = DBObject(el.as[JsObject].fields.toList)
      val writeResult = db(collection).insert(query)
      if(writeResult.wasAcknowledged()) {
        sender ! new Success(_: Unit)
        //TODO LOG WRITE SUCCESS
      } else {
        sender ! new Failure(new Exception("TODO"))
        //TODO LOG FAILURE
      }
    }
  }

  private def handleFind: Receive = {
    case _ @ Find(key, value, collection, single) => {
      val parseJson = (r: DBObject) => {Json.parse(r.toString)}
      val result = if(single) {
        val query = MongoDBObject(key -> value)
        db(collection).findOne(query).map(parseJson)
      } else {
        db(collection).find().toList.map(parseJson)
      }
      
      sender ! result
      context.stop(self)
    }
  }

  private def handleUpdate: Receive = {
    case req @ Update(_,_,_,_,_) => {
      sender ! List("TODO")
    }
  }

  private def handleDelete: Receive = {
    case req @ Delete(key, id, collection) => {
      val _sender = sender
      val query = MongoDBObject(key -> id)
      db(collection).findAndRemove(query) match {
        case Some(_) => {
          _sender ! new Success(_: Unit)
          //LOG SUCCESS
        }
        case None => {
          _sender ! new Failure(new Exception("TODO"))
          // LOG FAILURE
        }
      }
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

