package play.module.io.joaovasques.playspark.tests

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.google.inject._
import com.google.inject.name.Named
import config.ConfigModule
import play.module.io.joaovasques.playspark.akkaguice.AkkaModule
import play.module.io.joaovasques.playspark.spark.SparkMessages.DeleteContext
import play.module.io.joaovasques.playspark.spark.SparkMessages.GetContext
import play.module.io.joaovasques.playspark.spark.SparkMessages.RestartContext
import play.module.io.joaovasques.playspark.spark.SparkMessages.SaveContext
import play.module.io.joaovasques.playspark.spark.SparkMessages.StopContext
import play.module.io.joaovasques.playspark.spark.SparkModule
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import play.module.io.joaovasques.playspark.akkaguice.GuiceAkkaExtension
import play.module.io.joaovasques.playspark.core.CoreActor
import play.module.io.joaovasques.playspark.core.CoreModule
import play.module.io.joaovasques.playspark.execution.ExectionModule
import play.module.io.joaovasques.playspark.execution.JobExecutionMessages._
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

class SparkSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with WordSpecLike
    with Matchers with BeforeAndAfterAll {

  private final implicit val duration = 5.seconds
  private final implicit val timeout = Timeout(5 seconds)

  def this() = this(ActorSystem("SparkSpec"))
      
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }
      
  private var sparkAppid: String = _
      
  "A Spark Actor must" must {
    "submit a unique Spark correctly" in new Core {
      override def _sys = _system
      
      val conf = new SparkConf()
        .setMaster("local[2]")
        .setAppName("SparkSpec")
      val sc = new SparkContext(conf)
      sparkAppid = sc.applicationId

      val futureResult = (core ? new SaveContext(sc)).mapTo[Try[Unit]]
      sc.stop()
      val result = Await.result(futureResult, duration)
      result shouldBe an [Try[_]]
    }

    "fetch an existing Spark context" in new Core {
      override def _sys = _system

      val futureResult = (core ? new GetContext(sparkAppid)).mapTo[Option[SparkContext]]
      Await.result(futureResult, duration) match {
        case Some(sc) => {
          val result = sc.applicationId
          sc.stop()
          result === sparkAppid

        }
        case None => fail(s"Not Spark Context was found for app id ${sparkAppid}")
      }
    }

    "restart an existing Spark Context" in new Core {
      override def _sys = _system

      val futureResult = (core ? new RestartContext(sparkAppid)).mapTo[SparkContext]
      val result = Await.result(futureResult, duration)
      result.stop()
      result shouldBe an [SparkContext]
    }

    "stop an existing and running Spark Context" in new Core {
      override def _sys = _system

      val futureResult = (core ? new StopContext(sparkAppid)).mapTo[Try[Unit]]
      Await.result(futureResult, duration) shouldBe an [Success[_]]
    }

    "delete an existing Spark Context" in new Core {
      override def _sys = _system

      val futureResult = (core ? new DeleteContext(sparkAppid)).mapTo[Try[Unit]]
      Await.result(futureResult, duration) shouldBe an [Success[_]]
    }
  }
}

