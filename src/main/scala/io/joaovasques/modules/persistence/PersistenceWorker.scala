package play.modules.io.joaovasques.playspark.persistence

import akka.actor.{ActorLogging, Props, Actor}
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.modules.io.joaovasques.playspark.persistence.PersistenceMessages._
import com.mongodb.casbah.Imports._
import scala.util.Failure
import scala.util.Success

private[persistence] sealed class PersistenceWorker(db: MongoDB) extends Actor with ActorLogging {

  private def handleInsert: Receive = {
    case req @ Insert(el, collection) => {
      val _sender = sender
      val query = DBObject(el.as[JsObject].fields.toList)
      val writeResult = db(collection).insert(query)
      if(writeResult.wasAcknowledged()) {
        _sender ! new Success((): Unit)
        //TODO LOG WRITE SUCCESS
      } else {
        _sender ! new Failure(new Exception("TODO"))
        //TODO LOG FAILURE
      }
      context.stop(self)
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
          _sender ! new Success((): Unit)
          //LOG SUCCESS
        }
        case None => {
          _sender ! new Failure(new Exception("TODO"))
          // LOG FAILURE
        }
      }
      context.stop(self)
    }
  }

  private def unhandled: Receive = {
    case m => log.error("unhandled message: " + m)
  }

  def receive = handleInsert orElse handleFind orElse handleUpdate orElse handleDelete orElse unhandled
}

object PersistenceWorker {

  def props(client: MongoDB): Props = Props(new PersistenceWorker(client))
}

