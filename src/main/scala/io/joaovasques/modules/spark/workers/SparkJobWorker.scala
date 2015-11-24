package play.modules.io.joaovasques.playspark.spark.workers

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import java.util.Date
import org.apache.spark.SparkContext
import org.apache.spark.SparkEnv
import scala.concurrent.ExecutionContext
import play.modules.io.joaovasques.playspark.spark.SparkMessages._
import scala.concurrent.Future
import scala.util.{Failure, Success}

object SparkJobWorker {

  def props(
    jobRequester: ActorRef,
    exectionContext: ExecutionContext,
    context: SparkContext
  ): Props = Props(new SparkJobWorker(jobRequester, exectionContext, context))
}

private[spark] class SparkJobWorker(
  jobRequester: ActorRef,
  exectionContext: ExecutionContext,
  sparkContext: SparkContext 
) extends Actor with ActorLogging {

  import context._

  def receive = {
    case _ @ StartSparkJob(job, contextId, async) => {
      //TODO: send job information to status actor
      if(!async) {
        jobRequester ! new JobStarted(self.path.name)
      }

      Future {
        log.info(s"Starting job ${self.hashCode()} future")
        job.runJob(sparkContext)
      }(exectionContext).andThen{
        case Success(result: Any) => {
          val msg = new JobCompleted(self.path.name, result) 
          parent ! msg
          jobRequester ! msg
        }
        case Failure(ex: Throwable) => {
          val msg = new JobFailed(self.path.name, ex, new Date().getTime())
          parent ! msg
          jobRequester ! msg
        }
      }(exectionContext).andThen{
        case _ => {
          log.info("Job is done. Shutting down worker")
          context.stop(self)
        }
      }(exectionContext)
    }
  }
}

