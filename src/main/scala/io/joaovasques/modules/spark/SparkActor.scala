package play.module.io.joaovasques.playspark.spark

import akka.actor.{Actor, ActorRef, Props}
import play.module.io.joaovasques.playspark.akkaguice.NamedActor
import com.google.inject.{BindingAnnotation, Inject}
import com.google.inject.name.Named
import play.module.io.joaovasques.playspark.execution.JobExecutionMessages._
import play.module.io.joaovasques.playspark.persistence.PersistenceActor
import play.module.io.joaovasques.playspark.spark.SparkMessages._
import play.module.io.joaovasques.playspark.spark.SparkImplicits._
import play.module.io.joaovasques.playspark.persistence.PersistenceMessages._

object SparkActor extends NamedActor {
  override final val name = "SparkActor"
}

class SparkActor @Inject()(
  @Named(PersistenceActor.name) persistenceActor: ActorRef
) extends Actor {

  private def handleGetSparkContext: Receive = {
    case request @ GetContext(_) => {

    }
  }

  private def handleGetContexts: Receive = {
    case request @ GetContexts => {}
  }

  private def handleSaveContext: Receive = {
    case request @ SaveContext(_) => {
      persistenceActor ! new Insert(sparkConfToJson(request.context.getConf), "contexts")
      println(s"CONTEXT\n${sparkConfToJson(request.context.getConf)}")
    }
  }

  private def handleStopContext: Receive = {
    case request @ StopContext(_) => {}
  }

  private def handleRestartContext: Receive = {
    case request @ RestartContext(_) => {}
  }

  private def handleDeleteContext: Receive = {
    case request @ DeleteContext(_) => {}
  }

  private def unhandled: Receive = {
    case _ => {

    }
  }

  def receive = handleGetSparkContext orElse handleSaveContext orElse unhandled
  
}

