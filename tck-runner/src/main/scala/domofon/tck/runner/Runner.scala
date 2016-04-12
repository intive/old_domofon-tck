package domofon.tck.runner

import akka.http.scaladsl.model.Uri
import domofon.tck.DomofonTck

import scala.util.Try

object Runner extends App {
  if (args.isEmpty) {
    println("You need to provide hostname of target Server hosting Domofon")
    sys.exit(1)
  } else {

    class ExternalDomofonTck extends DomofonTck with ExternalServer {
      override protected def domofonUri: Uri = Uri(args.head)
    }

    Try {
      (new ExternalDomofonTck).execute(durations = true, stats = true, fullstacks = true)
    }.getOrElse(sys.exit(1))
  }

  sys.exit(0)

}
