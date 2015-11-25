package play.modules.io.joaovasques.playspark.core

import javax.inject.Inject

import akka.actor.{Actor, ActorRef, ActorSystem}
import play.modules.io.joaovasques.playspark.akkaguice.GuiceAkkaActorRefProvider
import com.google.inject.name.{Named, Names}
import com.google.inject.{AbstractModule, Provides, Singleton}
import net.codingwell.scalaguice.ScalaModule

class CoreModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {

  override def configure() {
    bind[Actor].annotatedWith(Names.named(CoreActor.name)).to[CoreActor]
  }

  @Provides
  @Singleton
  @Named(CoreActor.name)
  def provideCoreActorRef(@Inject() system: ActorSystem): ActorRef = provideActorRef(system, CoreActor.name)

}

