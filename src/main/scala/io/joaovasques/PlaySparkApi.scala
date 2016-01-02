package play.modules.io.joaovasques.playspark

import akka.actor.ActorRef
import com.google.inject.Injector
import javax.inject._
import akka.actor.ActorSystem
import play.api.Configuration
import play.api.Logger
import play.api.inject.ApplicationLifecycle
import scala.concurrent.Await
import scala.util.Try
import org.apache.spark.{SparkConf, SparkContext}
import scala.concurrent.Future
import play.modules.io.joaovasques.playspark.api.SparkJob
import annotation.implicitNotFound
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Guice
import play.modules.io.joaovasques.playspark.config._
import play.modules.io.joaovasques.playspark.akkaguice.AkkaModule
import play.modules.io.joaovasques.playspark.akkaguice.GuiceAkkaExtension
import play.modules.io.joaovasques.playspark.core.CoreActor
import play.modules.io.joaovasques.playspark.core.CoreModule
import play.modules.io.joaovasques.playspark.api.SparkJob
import play.modules.io.joaovasques.playspark.persistence._
import play.modules.io.joaovasques.playspark.spark.SparkModule
import play.modules.io.joaovasques.playspark.spark.SparkMessages._
import play.modules.io.joaovasques.playspark.stats.StatsModule
import net.codingwell.scalaguice.InjectorExtensions._
import scala.concurrent.ExecutionContext.Implicits.global

trait PlaySparkApi {

  // Context API
  def saveSparkContext(conf: SparkConf)(implicit timeout: Timeout): Future[Try[String]]
  def startContext(contextId: String)(implicit timeout: Timeout): Future[Try[Unit]]
  def getContextsConfig()(implicit timeout: Timeout): Future[List[SparkConf]]
  def deleteContext(contextId: String)(implicit timeout: Timeout): Future[Try[Unit]]
  def stopContext()(implicit timeout: Timeout): Future[Try[Unit]]

  // Job API
  def startJob(job: SparkJob, contextId: String)(implicit timeout: Timeout): Future[SparkJobResult]
  def startSyncJob(job: SparkJob, contextId: String)(implicit timeout: Timeout): SparkJobResult
}

final class PlaySparkApiImplementation @Inject() (
  actorSystem: ActorSystem,
  configuration: Configuration,
  applicationLifecycle: ApplicationLifecycle
) extends PlaySparkApi {

  private final val injector = Guice.createInjector(
    new ConfigModule(),
    new AkkaModule(),
    new PersistenceModule(),
    new StatsModule(),
    new SparkModule(),
    new CoreModule()
  )

  Logger.info("PlaySpark starting...")

  private final lazy val system = injector.instance[ActorSystem]
  private final lazy val coreActor = system.actorOf(GuiceAkkaExtension(system).props(CoreActor.name))

  private def registerDriverShutdown(
    coreActor: ActorRef,
    system: ActorSystem
  ): Unit = applicationLifecycle.addStopHook(() => {
    Logger.info("PlaySpark stopping..")
    Future {
      //TODO KILL OTHER ACTORS SMOOTHLY
      system.shutdown()
    }
  })

  registerDriverShutdown(coreActor, system)

  @implicitNotFound(msg = "Cannot find implicit timeout") 
  def saveSparkContext(conf: SparkConf)(implicit timeout: Timeout): Future[Try[String]] = {
    Logger.info("Save Spark Context Configuration")
    (coreActor ? new SaveContext(conf)).mapTo[Try[String]]
  }

  @implicitNotFound(msg = "Cannot find implicit timeout")
  def startContext(contextId: String)(implicit timeout: Timeout): Future[Try[Unit]] = {
    Logger.info(s"Starting Spark Context with id $contextId")
    (coreActor ? new StartContext(contextId)).mapTo[Try[Unit]]
  }

  @implicitNotFound(msg = "Cannot find implicit timeout")
  def getContextsConfig()(implicit timeout: Timeout): Future[List[SparkConf]] = {
    Logger.info("Getting all spark contexts config")
    (coreActor ? new GetContexts()).mapTo[List[SparkConf]]
  }

  @implicitNotFound(msg = "Cannot find implicit timeout")
  def deleteContext(contextId: String)(implicit timeout: Timeout): Future[Try[Unit]] = {
    Logger.info(s"Deleting spark context with id $contextId")
    (coreActor ? new DeleteContext(contextId)).mapTo[Try[Unit]]
  }

  @implicitNotFound(msg = "Cannot find implicit timeout")
  def stopContext()(implicit timeout: Timeout): Future[Try[Unit]] = {
    Logger.info("Stopping current spark context")
    (coreActor ? new StopContext()).mapTo[Try[Unit]]
  }

  @implicitNotFound(msg = "Cannot find implicit timeout")
  def startJob(job: SparkJob, contextId: String)(implicit timeout: Timeout): Future[SparkJobResult] = {
    Logger.info(s"Starting async job on context $contextId")
    (coreActor ? new StartSparkJob(job, contextId, true)).mapTo[SparkJobResult]
  }

  @implicitNotFound(msg = "Cannot find implicit timeout duration")
  def startSyncJob(job: SparkJob, contextId: String)(implicit timeout: Timeout): SparkJobResult = {
    Logger.info(s"Starting sync job context $contextId")
    val futureResult = (coreActor ? new StartSparkJob(job, contextId, true)).mapTo[SparkJobResult]
    Await.result(futureResult, timeout.duration)
  }
}

