package domofon.tck

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes, MediaTypes}
import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.testkit._
import domofon.tck.entities.PostContact
import org.scalatest.{FunSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try
import spray.json._

trait BaseTckTest extends FunSpec with Matchers with ScalatestRouteTest {

  implicit val routeTestTimeout: RouteTestTimeout = RouteTestTimeout(new DurationInt(30).second)

  val nonExistentUuid: UUID = UUID.fromString("00000000-0000-0000-0000-420000000000")

  def domofonRoute: Route

  def acceptPlain: HttpRequest => HttpRequest = addHeader(Accept(MediaTypes.`text/plain`))

  def acceptJson: HttpRequest => HttpRequest = addHeader(Accept(MediaTypes.`application/json`))

  def contactRequest(): PostContact = PostContact("John Smith", "Company Ltd.", "email@domain.pl", "+48123321123")

  def maxSampleLength: Int = 1000

  def postContactRequest(cr: PostContact = contactRequest()): UUID = {
    import DomofonMarshalling._
    import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
    import spray.json._
    var uuid: UUID = nonExistentUuid
    val ret = Post("/contacts", cr.toJson) ~> acceptPlain ~~> {
      status shouldBe StatusCodes.OK
      uuid = UUID.fromString(responseAs[String])
    }
    uuid
  }

  def formatedEntityString(content: String): String = {
    Try(content.parseJson.prettyPrint).getOrElse {
      val len = content.length
      if (len + 3 > maxSampleLength) {
        content.take(maxSampleLength) + "..."
      } else {
        content
      }
    }
  }

  def requestAsString(request: HttpRequest): String = {
    val content = Await.result(request.entity.dataBytes.runReduce(_ ++ _), Duration.Inf).utf8String

    s"""
       |${request.method.value} ${request.uri}
       |${request.headers.mkString("\n  ")}
       |Content-type: ${request.entity.contentType}
       |
       |
       |${formatedEntityString(content)}""".stripMargin
  }

  def responseAsString(response: HttpResponse): String = {
    val content = Await.result(response.entity.dataBytes.runReduce(_ ++ _), Duration.Inf).utf8String
    s"""
       |${response.status.value}
       |${response.headers.mkString("\n  ")}
       |Content-type: ${response.entity.contentType}
       |
       |
       |${formatedEntityString(content)}""".stripMargin
  }

  implicit class PimpedRequest(request: HttpRequest) {
    /**
     * Shortcut to run request using domofonRoute
     *
     * Prints nicely Request and Response in case of error
     */
    def ~~>[T](body: => T) = request ~> domofonRoute ~> check {
      try {
        val a = body
        a
      } catch {
        case t: Throwable =>
          info("  Request was:")
          info("/--------------------------------------\\")
          info(requestAsString(request))
          info("\\--------------------------------------/")
          info("  Response was:")
          info("/--------------------------------------\\")
          info(responseAsString(response))
          info("\\--------------------------------------/")
          throw t
      }
    }
  }

}
