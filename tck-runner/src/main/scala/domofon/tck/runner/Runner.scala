package domofon.tck.runner

import akka.http.scaladsl.model.Uri
import domofon.tck.{TckEnvCredentials, DomofonTck}

import scala.util.Try

object Runner extends App {
  val runnerHeader = s"tck-runner - Domofon API TCK tests runner"
  println(runnerHeader)

  if (args.isEmpty) {
    println("You need to provide hostname of target Server hosting Domofon eg. http://localhost:8080")
    sys.exit(1)
  } else {

    class ExternalDomofonTck extends DomofonTck with ExternalServer with TckEnvCredentials {
      override protected def domofonUri: Uri = Uri(args.head)
      println(s"Using admin login: $tckAdminLogin and password: $tckAdminPass")
      println(s"You can change them using ${TckEnvCredentials.AdminLoginEnvName} " +
        s"and ${TckEnvCredentials.AdminPasswordEnvName} environment variables.")
    }

    Try {
      (new ExternalDomofonTck).execute(durations = true, stats = true)
    }.getOrElse(sys.exit(1))
  }

  sys.exit(0)

}
