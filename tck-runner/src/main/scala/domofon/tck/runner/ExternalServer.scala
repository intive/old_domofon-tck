package domofon.tck.runner

import akka.http.scaladsl._
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.server._
import akka.stream.scaladsl.{Sink, Source}
import domofon.tck.DomofonTck

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

  private[this] def proxyResult: Route = Route { context =>
    val uri = domofonUri
    val request = context.request
    val flow = Http(system).outgoingConnection(uri.authority.host.address(), uri.authority.port)
    val handler = Source.single(preprocessRequest(context.request))
      .via(flow)
      .runWith(Sink.head)
      .flatMap(context.complete(_))
    handler
  }

  def domofonRoute: Route = Route.seal(proxyResult)

}
