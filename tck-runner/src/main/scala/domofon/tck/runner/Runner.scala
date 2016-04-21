package domofon.tck.runner

import akka.http.scaladsl.model.Uri
import domofon.tck.DomofonTck

import scala.util.Try

object Runner extends App {
  val runnerHeader = s"${BuildInfo.name} (${BuildInfo.version}) - Domofon API TCK tests runner"
  println(runnerHeader)

  if (args.isEmpty) {
    println("You need to provide hostname of target Server hosting Domofon")
    sys.exit(1)
  } else {

    class ExternalDomofonTck extends DomofonTck with ExternalServer {
      override protected def domofonUri: Uri = Uri(args.head)
      println(s"Using admin login: $TckAdminLogin and password: $TckAdminPass")
      println(s"You can change them using $AdminLoginEnvName and $AdminPasswordEnvName environment variables.")
    }

    Try {
      (new ExternalDomofonTck).execute(durations = true, stats = true)
    }.getOrElse(sys.exit(1))
  }

  sys.exit(0)

}
