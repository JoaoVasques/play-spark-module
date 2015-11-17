package play.module.io.joaovasques.playspark.tests

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.google.inject._
import com.google.inject.name.Named
import config.ConfigModule
import play.module.io.joaovasques.playspark.akkaguice.AkkaModule
import play.module.io.joaovasques.playspark.api.SparkJob
import play.module.io.joaovasques.playspark.spark.SparkMessages._
import play.module.io.joaovasques.playspark.spark.SparkModule
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import play.module.io.joaovasques.playspark.akkaguice.GuiceAkkaExtension
import play.module.io.joaovasques.playspark.core.CoreActor
import play.module.io.joaovasques.playspark.core.CoreModule
import play.module.io.joaovasques.playspark.persistence.PersistenceModule
import play.module.io.joaovasques.playspark.stats.StatsModule

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration._
import play.module.io.joaovasques.playspark.tests.TestTraits._
import org.apache.spark.{SparkContext, SparkConf}
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Failure
import scala.util.Success
import scala.util.Try

sealed class ExampleJob extends SparkJob {

  def runJob(context: SparkContext): Any = {
    println("HELLO JOB")
  }
}

class SparkJobSubmissionSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with WordSpecLike
    with Matchers with BeforeAndAfterAll {

  private final implicit val duration = 5.seconds
  private final implicit val timeout = Timeout(5 seconds)

  def this() = this(ActorSystem("SparkJobSubmissionSpec"))
      
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  // "A Spark Actor" must {
  //   "start a job" in new Core {
  //     override def _sys = _system

  //     // val sc = new SparkContext({
  //     //   new SparkConf()
  //     //   .setMaster("local[2]")
  //     //   .setAppName("SparkSpec")
  //     // })
  //     // sc.stop()

  //     val request = new StartSparkJob(new ExampleJob(), "")//sc.applicationId)
  //     core ! request
  //     Thread.sleep(3000)
  //     1 === 1
  //   }
  // }
}

