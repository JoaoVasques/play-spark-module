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

    "should not be able to stop a non running spark context" >> {
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
