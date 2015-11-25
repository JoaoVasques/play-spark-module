package play.modules.io.joaovasques.playspark

import javax.inject._

import akka.actor.ActorSystem
import play.api.ApplicationLoader.Context
import play.api._
import play.api.inject.{ ApplicationLifecycle, Binding, Module }

/**
 * PlaySparkModule module.
 */
@Singleton
final class PlaySparkModule extends Module {
  override def bindings(
    environment: Environment,
    configuration: Configuration
  ): Seq[Binding[_]] = Seq(bind[PlaySparkApi].to[PlaySparkApiImplementation].in[Singleton])
}

