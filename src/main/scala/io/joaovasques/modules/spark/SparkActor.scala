package play.module.io.joaovasques.playspark.spark

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import scala.concurrent.duration._
import scala.language.postfixOps
import akka.util.Timeout
import play.api.libs.json.JsValue
import play.module.io.joaovasques.playspark.akkaguice.NamedActor
import com.google.inject.{BindingAnnotation, Inject}
import com.google.inject.name.Named
import play.module.io.joaovasques.playspark.execution.JobExecutionMessages._
import play.module.io.joaovasques.playspark.persistence.PersistenceActor
import play.module.io.joaovasques.playspark.spark.SparkMessages._
import play.module.io.joaovasques.playspark.spark.SparkImplicits._
import play.module.io.joaovasques.playspark.persistence.PersistenceMessages._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure
import scala.util.Success
import reflect.ClassTag
import scala.util.Try

object SparkActor extends NamedActor {
  override final val name = "SparkActor"
}

class SparkActor @Inject()(
  @Named(PersistenceActor.name) persistenceActor: ActorRef
) extends Actor {

  private final implicit val timeout = Timeout(5 seconds)

  private def getSparkContext[T](query: Find)(onSuccess : T => Unit )(onFailure: Throwable => Unit)(implicit tag: ClassTag[T] ): Unit = {
    (persistenceActor ? query).mapTo[T].onComplete{
      case Success(s) => onSuccess(s)
      case Failure(f) => onFailure(f)
    }
  }

  private def sendTryResponse[T](result: Try[_], sender: ActorRef, successResponse: T): Unit = {
    result match {
      case Success(_) => sender ! successResponse
      case Failure(ex) => sender ! ex
    }
  }

  private def handleGetSparkContext: Receive = {
    case request @ GetContext(id) => {
      val _sender = sender
      val query = new Find("spark_app_id", id, "contexts")
      getSparkContext[Option[JsValue]](query){result: Option[JsValue] =>
        _sender ! ( result.map(r => new SparkContext(r: SparkConf)))
      } {failure: Throwable =>
         _sender ! failure
      }
    }
  }

  private def handleGetContexts: Receive = {
    case request @ GetContexts => {
      val _sender = sender
      val query = new Find("", "", "contexts")
      getSparkContext[List[JsValue]](query) {result =>
        _sender ! result.map(c => c: SparkConf)
      } {failure =>
        _sender ! failure
      }
    }
  }

  private def handleSaveContext: Receive = {
    case request @ (_: SaveContext) => {
      val _sender = sender
      val query = new Insert(sparkConfToJson(request.context.getConf), "contexts")
      val successResponse = new Success((): Unit)
        (persistenceActor ? query).mapTo[Try[Unit]].onComplete(
          sendTryResponse[Success[Unit]](_, _sender, successResponse)
        )
    }
  }

  private def handleStopContext: Receive = {
    case request @ StopContext(id) => {
      val _sender = sender
      val query = new Find("spark_app_id", id, "contexts")
      getSparkContext[Option[JsValue]](query) {result =>
        result match {
          case Some(config) => {
            val sparkContext = new SparkContext(config: SparkConf)
            sparkContext.stop()
            _sender ! new Success(_: Unit)
          }
          case None => {
            _sender ! new Failure(new Exception("TODO"))
            //TODO log that context was not found
          }
        }
      } {failure =>
         _sender ! failure
        //TODO log that context was not found
      }
    }
  }

  private def handleRestartContext: Receive = {
    case request @ RestartContext(id) => {
      val _sender = sender
      val query = new Find("spark_app_id", id, "contexts")
      getSparkContext[Option[JsValue]](query) {
        case Some(result) => {
          _sender ! ( new SparkContext(result: SparkConf))
        }
        case None => {}
      } {failure =>
        _sender ! failure
      }
    }
  }

  //FIND AND STOP CONTEXT
  private def handleDeleteContext: Receive = {
    case request @ DeleteContext(id) => {
      val _sender = sender
      val query = new Find("spark_app_id", id, "contexts")
      getSparkContext[Option[JsValue]](query) {
        case Some(result) => {
          val sparkContext = new SparkContext(result: SparkConf)
          sparkContext.stop()
          val deleteQuery = new Delete("spark_app_id", id, "contexts")
            (persistenceActor ? deleteQuery).mapTo[Try[Unit]].onComplete(
              sendTryResponse[Success[Unit]](_, _sender, new Success((): Unit))
            )
        }
        case None => {
          _sender ! new Failure(new Exception("TODO"))
          // TODO send exception
        }
      } {failure =>
        _sender ! failure
      }
    }
  }

  private def unhandled: Receive = {
    case _ => {

    }
  }

  def receive = handleGetSparkContext orElse handleSaveContext orElse unhandled
}

