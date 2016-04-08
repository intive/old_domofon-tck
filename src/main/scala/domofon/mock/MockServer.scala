package domofon.mock

import akka.http.scaladsl.server.{Directives, Route}

trait MockServer extends Directives{

  def domofonRoute: Route = complete("OK")


}
