package play.module.io.joaovasques.playspark.persistence

import javax.inject.Inject

import akka.actor.{Actor, ActorRef, ActorSystem}
import play.module.io.joaovasques.playspark.akkaguice.GuiceAkkaActorRefProvider
import com.google.inject.name.{Named, Names}
import com.google.inject.{AbstractModule, Provides, Singleton}
import net.codingwell.scalaguice.ScalaModule

/**
 * A Guice module for the audit actors.
 *
 * This module provides top level actors for wiring into other actor constructors. Top level actors should be
 * used sparingly and only to wire-up few top-level components.
 */
class PersistenceModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {

  override def configure() {
    bind[Actor].annotatedWith(Names.named(PersistenceActor.name)).to[PersistenceActor]
  }

  @Provides
  @Singleton
  @Named(PersistenceActor.name)
  def provideJobExecutionActorRef(@Inject() system: ActorSystem): ActorRef = provideActorRef(system, PersistenceActor.name)

}
