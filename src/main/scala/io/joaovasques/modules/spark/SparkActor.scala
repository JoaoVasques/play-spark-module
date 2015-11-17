package play.module.io.joaovasques.playspark.spark

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.routing.ActorRefRoutee
import akka.routing.Router
import java.util.UUID
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps
import akka.util.Timeout
import play.api.libs.json.JsValue
import play.module.io.joaovasques.playspark.akkaguice.NamedActor
import com.google.inject.{BindingAnnotation, Inject}
import com.google.inject.name.Named
import play.module.io.joaovasques.playspark.persistence.PersistenceActor
import play.module.io.joaovasques.playspark.spark.SparkMessages._
import play.module.io.joaovasques.playspark.spark.SparkImplicits._
import play.module.io.joaovasques.playspark.persistence.PersistenceMessages._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure
import scala.util.Success
import reflect.ClassTag
import scala.util.Try
import play.module.io.joaovasques.playspark.spark.workers.{SparkJobWorker}
import java.util.concurrent.Executors._


object SparkActor extends NamedActor {
  override final val name = "SparkActor"
}

class SparkActor @Inject()(
  @Named(PersistenceActor.name) persistenceActor: ActorRef
) extends Actor {

  private var currentSparkContext: SparkContext = null
  private final val maximumRunningJobs = Runtime.getRuntime.availableProcessors
  private val executionContext: ExecutionContext = ExecutionContext.fromExecutorService(newFixedThreadPool(maximumRunningJobs))
  private final implicit val timeout = Timeout(5 seconds)

  private def getSparkContext[T](query: Find)(onSuccess : T => Unit )(onFailure: Throwable => Unit)(implicit tag: ClassTag[T] ): Unit = {
    (persistenceActor ? query).mapTo[T].onComplete{
      case Success(s) => onSuccess(s)
      case Failure(f) => onFailure(f)
    }
  }

  private def sendTryResponse[T](result: Try[_], s: ActorRef, successResponse: T): Unit = {
    result match {
      case Success(_) => s ! successResponse
      case Failure(ex) => s ! ex
    }
  }

  private def handleGetContexts: Receive = {
    case request @ GetContexts() => {
      val _sender = sender
      val query = new Find("", "", "contexts", false)
      getSparkContext[List[JsValue]](query) {result =>
        _sender ! result.map(c => c: SparkConf)
      } {failure =>
        println(failure.getStackTraceString)
        _sender ! failure
      }
    }
  }

  private def handleSaveContext: Receive = {
    case request @ (_: SaveContext) => {
      val _sender = sender
      if(currentSparkContext == null) {
        currentSparkContext = new SparkContext(request.conf)
      }

      val query = new Insert(sparkConfToJson(currentSparkContext.getConf), "contexts")
      val successResponse = new Success((): Unit)
        (persistenceActor ? query).mapTo[Try[Unit]].onComplete(sendTryResponse[Success[Unit]](_, _sender, successResponse))
    }
  }

  private def handleStopContext: Receive = {
    case request @ StopContext() => {
      if(currentSparkContext != null) {
        currentSparkContext.stop()
        currentSparkContext = null
        sender ! new Success((): Unit)
      } else {
        sender ! new Failure(new Exception("No running context"))
      }
    }
  }

  private def handleStartContext: Receive = {
    case request @ StartContext(id) => {
      val _sender = sender
      val query = new Find("spark_app_id", id, "contexts")
      getSparkContext[Option[JsValue]](query) {
        case Some(result) => {
          currentSparkContext = new SparkContext(result: SparkConf)
          _sender ! new Success((): Unit)
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
      if(currentSparkContext != null) {
        if(currentSparkContext.applicationId == id) {
          currentSparkContext.stop()
          currentSparkContext = null
        }
      }

      val _sender = sender
      val query = new Find("spark_app_id", id, "contexts")
      getSparkContext[Option[JsValue]](query) {
        case Some(_) => {
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

  private def handleJobSubmission: Receive = {
    case message : SparkJobMessage => {
      val workerId = UUID.randomUUID().toString()
      context.actorOf(SparkJobWorker.props(executionContext), name = s"SparkJobWorker-${workerId}") forward message
    }
  }

  def receive = handleGetContexts orElse
    handleSaveContext orElse
    handleStopContext orElse
    handleStartContext orElse
    handleDeleteContext orElse
    handleJobSubmission
  
}

