package domofon.tck

import java.util.UUID

import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.Unmarshal
import domofon.tck.entities.ContactRequest
import org.scalatest.{FunSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._

trait BaseTckTest extends FunSpec with Matchers with ScalatestRouteTest {
  def domofonRoute: Route

  def acceptPlain = addHeader(Accept(MediaTypes.`text/plain`))

  def acceptJson = addHeader(Accept(MediaTypes.`application/json`))

  def contactRequest() = ContactRequest("John", "Smith", "email@domain.pl", "+48123321123")

  def postContactRequest(cr: ContactRequest = contactRequest()): UUID = {
    import DomofonMarshalling._
    import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
    import spray.json._
    val ret = Post("/contacts", cr.toJson) ~> domofonRoute
    Await.result(Unmarshal(ret.response).to[UUID], 1.minute)
  }

}
