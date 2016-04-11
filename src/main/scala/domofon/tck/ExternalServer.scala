package domofon.tck

import akka.http.scaladsl._
import akka.http.scaladsl.model.headers.Host
import akka.http.scaladsl.model.{Uri, HttpRequest}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._

import scala.util.{Failure, Success}

trait ExternalServer {
  self: DomofonTck =>

  protected def domofonUri: Uri

  protected def preprocessRequest(req: HttpRequest): HttpRequest = {
    val targetUri = domofonUri
    val uri = req.uri.withAuthority(targetUri.authority).withScheme(targetUri.scheme)
    val headers = req.headers.filter(_.isNot("host"))
    val result = req.withUri(uri).withHeaders(headers)
    result
  }

  private[this] def proxyResult: Route = {
    extractRequest {
      req =>
        onComplete(Http().singleRequest(preprocessRequest(req))) {
          case Success(resp) => complete(resp)
          case Failure(e)    => throw e
        }
    }
  }

  def domofonRoute: Route = Route.seal(proxyResult)

}
