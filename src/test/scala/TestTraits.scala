package play.module.io.joaovasques.playspark.tests

import akka.actor.ActorSystem
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import net.codingwell.scalaguice.ScalaModule
import play.module.io.joaovasques.playspark.akkaguice.GuiceAkkaExtension
import play.module.io.joaovasques.playspark.core.CoreActor
import play.module.io.joaovasques.playspark.core.CoreModule
import com.google.inject._
import com.google.inject.name.Named
import play.module.io.joaovasques.playspark.persistence.PersistenceModule
import play.module.io.joaovasques.playspark.spark.SparkModule
import play.module.io.joaovasques.playspark.stats.StatsModule
import scala.collection.JavaConverters._


private[tests] object TestTraits {

  trait AkkaGuiceInjector {

    def _sys: ActorSystem

    var injector: Injector = _

    def initInjector(system: ActorSystem, testModules: Module*) = {
      val modules = List(
        new AbstractModule with ScalaModule {
          override def configure(): Unit = { }
          @Provides
          def provideSystem() = _sys
        }
      ) ++ testModules

      injector = Guice.createInjector(modules.asJava)
      GuiceAkkaExtension(system).initialize(injector)
    }
  }

  trait Core extends AkkaGuiceInjector {
    initInjector(
      _sys,
      new PersistenceModule(),
      new StatsModule(),
      new SparkModule(),
      new CoreModule()
    )
    val core = _sys.actorOf(GuiceAkkaExtension(_sys).props(CoreActor.name))
  }

}
