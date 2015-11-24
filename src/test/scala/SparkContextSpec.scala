package play.modules.io.joaovasques.playspark.tests

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.google.inject._
import com.google.inject.name.Named
import play.modules.io.joaovasques.playspark.config._
import play.modules.io.joaovasques.playspark.akkaguice.AkkaModule
import play.modules.io.joaovasques.playspark.spark.SparkMessages._
import play.modules.io.joaovasques.playspark.spark.SparkModule
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import play.modules.io.joaovasques.playspark.akkaguice.GuiceAkkaExtension
import play.modules.io.joaovasques.playspark.core.CoreActor
import play.modules.io.joaovasques.playspark.core.CoreModule
import play.modules.io.joaovasques.playspark.persistence.PersistenceModule
import play.modules.io.joaovasques.playspark.stats.StatsModule

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration._
import play.modules.io.joaovasques.playspark.tests.TestTraits._
import org.apache.spark.{SparkContext, SparkConf}
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Failure
import scala.util.Success
import scala.util.Try

class SparkContextSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with WordSpecLike
    with Matchers with BeforeAndAfterAll with Core {

  private final implicit val duration = 5.seconds
  private final implicit val timeout = Timeout(5 seconds)

  def this() = this(ActorSystem("SparkContextSpec"))

  override def _sys = _system

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }
      
  private var sparkAppid: String = _

  // "A Spark Actor" must {
  //   "submit a unique Spark Context correctly" in {
  //     val conf = new SparkConf()
  //       .setMaster("local[2]")
  //       .setAppName("SparkSpec")

  //     val futureResult = (core ? new SaveContext(conf)).mapTo[Try[Unit]]
  //     val result = Await.result(futureResult, duration)
  //     result shouldBe an [Try[_]]
  //   }

  //   "stop an existing, and running, Spark Context" in {
  //     val futureResult = (core ? new StopContext()).mapTo[Try[Unit]]
  //     Await.result(futureResult, duration) shouldBe an [Success[_]]
  //   }

  //   "not stop an unexisting Spark Context" in {
  //     val futureResult = (core ? new StopContext()).mapTo[Try[Unit]]
  //     Await.result(futureResult, duration) shouldBe an [Failure[_]]
  //   }

  //   "fetch one spark context if there's only one stored" in {
  //     val futureResult = (core ? new GetContexts()).mapTo[List[SparkConf]]
  //     val confs = Await.result(futureResult, duration)
  //     confs.size === 1
  //   }
  // }
}

