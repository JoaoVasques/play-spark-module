package play.modules.io.joaovasques.playspark.tests

import com.google.inject
import play.api.inject.guice.GuiceApplicationBuilder

import play.api.test.FakeApplication
import play.api.test.Helpers._
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._
import play.modules.io.joaovasques.playspark.{PlaySparkApi, PlaySparkApiImplementation}
import org.specs2.time.NoTimeConversions
import org.specs2.matcher._
import org.specs2.execute._
import scala.util.{Failure, Success}
import org.apache.spark.{SparkContext, SparkConf}
import play.modules.io.joaovasques.playspark.spark.SparkMessages._

object PlaySpec extends org.specs2.mutable.Specification with NoTimeConversions{
  "Play integration" title

  "PlaySpark API" should {

    def createRunnableFakeApplication[T]()(testBlock:(PlaySparkApiImplementation) => T) = {
      running(FakeApplication()) {
        configuredAppBuilder.injector.instanceOf[PlaySparkApi] match {
          case api: PlaySparkApiImplementation => testBlock(api)
          case _ => failure("module not loaded correctly")
        }
      }
    }

    "not be resolved if the module is not enabled" in running(
      FakeApplication()) {
        val appBuilder = new GuiceApplicationBuilder().build

        appBuilder.injector.instanceOf[PlaySparkApi].
          aka("resolution") must throwA[inject.ConfigurationException]
      }

    "be resolved if the module is enabled" in {
      running(FakeApplication()) {
        configuredAppBuilder.injector.instanceOf[PlaySparkApi] match {
          case api: PlaySparkApiImplementation => ok
        }
      }
    }

    "save a spark context and stop it" >> {
      running(FakeApplication()) {
        configuredAppBuilder.injector.instanceOf[PlaySparkApi] match {
          case api: PlaySparkApiImplementation => {
            val timeout = Timeout(5 seconds)
            val conf = new SparkConf()
              .setMaster("local[2]")
              .setAppName("PlaySpec")

            val result = Await.result(api.saveSparkContext(conf)(timeout), timeout.duration)
            result must beSuccessfulTry
            Await.result(api.deleteContext(result.get)(timeout), timeout.duration) must beSuccessfulTry
          }
          case _ => ko("module not loaded correctly")
        }
      }
    }

    "get an empty list when no spark contexts are saved" >> {
      running(FakeApplication()) {
        configuredAppBuilder.injector.instanceOf[PlaySparkApi] match {
          case api: PlaySparkApiImplementation => {
            val timeout = Timeout(5 seconds)
            val result = Await.result(api.getContextsConfig()(timeout), timeout.duration)
            result must be empty
          }
          case _ => ko("module not loaded correctly")
        }
      }
    }

    "not be able to stop a non running spark context" >> {
      //System.setProperty("config.resource", "test.conf")
      running(FakeApplication()) {
        configuredAppBuilder.injector.instanceOf[PlaySparkApi] match {
          case api: PlaySparkApiImplementation => {
            val timeout = Timeout(5 seconds)
            val futureResult = api.stopContext()(timeout)
            Await.result(futureResult, timeout.duration) must beFailedTry
          }
          case _ => ko("module not loaded correctly")
        }
      }
    }

    "not be able to start a job if no spark context is running" >> {
      //System.setProperty("config.resource", "test.conf")
      running(FakeApplication()) {
        configuredAppBuilder.injector.instanceOf[PlaySparkApi] match {
          case api: PlaySparkApiImplementation => {
            val timeout = Timeout(5 seconds)
            val job = new LongPiJob()
            val futureResult = api.startJob(job, "")(timeout)
            Await.result(futureResult, timeout.duration) must beAnInstanceOf[JobFailed]
          }
          case _ => ko("module not loaded correctly")
        }
      }
    }
  }

  def configuredAppBuilder = {
    import scala.collection.JavaConversions.iterableAsScalaIterable

    val env = play.api.Environment.simple(mode = play.api.Mode.Test)
    val config = play.api.Configuration.load(env)
    val modules = config.getStringList("play.modules.enabled").fold(
      List.empty[String])(l => iterableAsScalaIterable(l).toList)

    new GuiceApplicationBuilder().
      configure("play.modules.enabled" -> (modules :+
        "play.modules.io.joaovasques.playspark.PlaySparkModule")).build
  }
}
