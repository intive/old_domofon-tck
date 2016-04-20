package domofon.mock.akka

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait SwaggerRoute {

  implicit def system: ActorSystem
  implicit def materializer: Materializer
  private[this] implicit def executionContext: ExecutionContext = system.dispatcher

  def domofonYmlRoute: Route = {
    path("domofon.yaml") {
      get {
        onComplete(
          Http().singleRequest(
            Get("https://raw.githubusercontent.com/blstream/domofon-api/gh-pages/domofon.yaml")
          ).flatMap {
              req => Unmarshal(req).to[String]
            }
        ) {
            case Success(r) => complete(r)
            case Failure(f) => throw f
          }
      }
    }
  }

}
