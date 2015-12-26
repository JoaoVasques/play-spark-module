package play.modules.io.joaovasques.playspark.persistence

import javax.inject.Inject

import akka.actor.{Actor, ActorRef, ActorSystem}
import play.modules.io.joaovasques.playspark.akkaguice.GuiceAkkaActorRefProvider
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
    bind[Actor].annotatedWith(Names.named(MyPersistenceActor.name)).to[MyPersistenceActor]
  }

  @Provides
  @Singleton
  @Named(MyPersistenceActor.name)
  def provideJobExecutionActorRef(@Inject() system: ActorSystem): ActorRef = provideActorRef(system, MyPersistenceActor.name)

}
