package domofon.tck

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer

object Runner extends App {
  if (args.isEmpty) {
    println("You need to provide hostname of target Server hosting Domofon")
    sys.exit(1)
  } else {

    class ExternalDomofonTck extends DomofonTck with ExternalServer {
      override protected def domofonUri: Uri = Uri(args.head)
    }

    (new ExternalDomofonTck).execute(durations = true, stats = true)
  }

  sys.exit(0)

}
