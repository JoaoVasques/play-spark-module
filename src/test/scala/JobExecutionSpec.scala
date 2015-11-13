package play.module.io.joaovasques.playspark.tests

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.google.inject._
import com.google.inject.name.Named
import config.ConfigModule
import play.module.io.joaovasques.playspark.akkaguice.AkkaModule
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
import scala.concurrent.duration._
import play.module.io.joaovasques.playspark.tests.TestTraits._

class JobExecutionSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with WordSpecLike
    with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("JobExecutionSpec"))

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A Job Execution Actor must" must {
    "submit a job correctly" in new Core {
      override def _sys = _system
      implicit val duration = 5.seconds
      val futureResult = (core ? new StartJob("job-id"))(duration).mapTo[JobStarted]
      val result = Await.result(futureResult, duration)
      result shouldBe an [JobStarted]
    }
  }
}
