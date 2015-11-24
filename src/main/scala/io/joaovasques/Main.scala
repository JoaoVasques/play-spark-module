import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Guice
import play.modules.io.joaovasques.playspark.config._
import net.codingwell.scalaguice.InjectorExtensions._
import org.apache.spark.SparkContext
import play.modules.io.joaovasques.playspark.akkaguice.AkkaModule
import play.modules.io.joaovasques.playspark.akkaguice.GuiceAkkaExtension
import play.modules.io.joaovasques.playspark.core.CoreActor
import play.modules.io.joaovasques.playspark.core.CoreModule
import play.modules.io.joaovasques.playspark.api.SparkJob
import play.modules.io.joaovasques.playspark.persistence._
import play.modules.io.joaovasques.playspark.spark.SparkModule
import play.modules.io.joaovasques.playspark.spark.SparkMessages._
import play.modules.io.joaovasques.playspark.stats.StatsModule
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

class ExampleJob extends SparkJob {

  def runJob(context: SparkContext): Any = {
    println("Hello world")
  }
}

/**
 * A main class to start up the application.
 */
object Main extends App {

  val injector = Guice.createInjector(
    new ConfigModule(),
    new AkkaModule(),
    new PersistenceModule(),
    new StatsModule(),
    new SparkModule(),
    new CoreModule()
  )

  // implicit val timeout = Timeout(5 seconds)
  // val system = injector.instance[ActorSystem]
  // val core = system.actorOf(GuiceAkkaExtension(system).props(CoreActor.name))
  // val future = (core ? new StartSparkJob(new ExampleJob())).mapTo[Boolean]
  // Await.result(future, 3.seconds)

  // system.shutdown()
  // system.awaitTermination()

  // // this could be called inside a supervisor actor to create a supervisor hierarchy,
  // // using context.actorOf(GuiceAkkaExtension(context.system)...
  //val counter = system.actorOf(GuiceAkkaExtension(system).props(CountingActor.name))

  // // tell it to count three times
  //counter ! Count
  // counter ! Count
  // counter ! Count

  // // Create a second counter to demonstrate that `AuditCompanion` is injected under Prototype
  // // scope, which means that every `CountingActor` will get its own instance of `AuditCompanion`.
  // // However `AuditBus` is injected under Singleton scope. Therefore every `AuditCompanion`
  // // will get a reference to the same `AuditBus`.
  // val counter2 = system.actorOf(GuiceAkkaExtension(system).props(CountingActor.name))
  // counter2 ! Count
  // counter2 ! Count

  // // print the result
  // for {
  //   actor <- Seq(counter, counter2)
  //   result <- actor.ask(Get)(3.seconds).mapTo[Int]
  // } {
  //   println(s"Got back $result from $counter")
  // }

  //system.shutdown()
  //system.awaitTermination()
}
