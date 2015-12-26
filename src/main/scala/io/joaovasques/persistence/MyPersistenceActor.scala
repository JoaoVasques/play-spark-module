package play.modules.io.joaovasques.playspark.persistence

import akka.actor.{Actor, ActorRef, Props}
import java.util.UUID
import play.api.libs.json.JsValue
import play.modules.io.joaovasques.playspark.akkaguice.NamedActor
import com.google.inject.{BindingAnnotation, Inject}
import com.google.inject.name.Named
import play.modules.io.joaovasques.playspark.persistence.PersistenceMessages._
import scala.util.{Try, Success, Failure}

object MyPersistenceActor extends NamedActor {
  override final val name = "PersistenceActor"
}

private[persistence] sealed trait KVDatastore[T] {

  var store = List[T]()

  def addElement(el: T): Try[Unit] = {
    store.synchronized {
      if(!store.contains(el)) {
        store = el :: store
        new Success((): Unit)
      } else {
        new Failure(new Exception("TODO"))
      }
    }
  }

  def findElement(key: String, value: String): Option[T]
  def delete(key: String, value: String): Try[Unit]
}

private[persistence] sealed case class JsonDatastore() extends KVDatastore[JsValue] {

  def find(key: String, value: String) = {
    if(key == "" && value == "") {
      this.store
    } else {
      this.findElement(key, value)
    }
  }

  override def findElement(key: String, value: String): Option[JsValue] = {
    store.synchronized {
      this.store.find {el => (el \ key).as[String] == value}
    }
  }

  override def delete(key: String, value: String): Try[Unit] = {
    store.synchronized {
      findElement(key, value) match {
        case Some(el) => {
          this.store = this.store diff List(el)
          new Success((): Unit)
        }
        case None => new Failure(new Exception("TODO"))
      }
    }
  }
}

class MyPersistenceActor extends Actor  {

  private val datastore = new JsonDatastore()

  def receive = {
    case findRequest: Find => sender ! datastore.find(findRequest.key, findRequest.value)
    case insertRequest: Insert => sender ! datastore.addElement(insertRequest.element)
    case updateRequest: Update => {}
    case deleteRequest: Delete => sender ! datastore.delete(deleteRequest.key, deleteRequest.value)
  }
}

