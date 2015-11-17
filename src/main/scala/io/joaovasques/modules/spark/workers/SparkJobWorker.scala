package play.module.io.joaovasques.playspark.spark.workers

import akka.actor.Actor
import akka.actor.Props
import org.apache.spark.SparkEnv
import scala.concurrent.ExecutionContext
import play.module.io.joaovasques.playspark.spark.SparkMessages._

object SparkJobWorker {

  def props(exectionContext: ExecutionContext): Props = Props(new SparkJobWorker(exectionContext))
}

private[spark] class SparkJobWorker(
  exectionContext: ExecutionContext
) extends Actor {

  private var sparkEnv: SparkEnv = _

  def receive = {
    case _ @ StartSparkJob(job, contextId) => {
      sparkEnv = SparkEnv.get
      println("SPARK JOB WORKER")
      println(sparkEnv)
      sender ! true
      context.stop(self)
    }
  }
}

