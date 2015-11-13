package play.module.io.joaovasques.playspark.execution

import akka.actor.Kill
import akka.actor.{Actor, ActorRef}
import play.module.io.joaovasques.playspark.akkaguice.NamedActor
import com.google.inject.{BindingAnnotation, Inject}
import com.google.inject.name.Named
import play.module.io.joaovasques.playspark.execution.JobExecutionMessages._

private[execution] sealed class JobWorker extends Actor {

  def receive = {
    case job @ StartJob(_) => {
      println(s"received ${job} from ${sender}")
      context.stop(self)
    }
  }
}

