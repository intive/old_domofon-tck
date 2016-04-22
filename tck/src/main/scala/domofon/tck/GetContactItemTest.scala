package domofon.tck


import domofon.tck.DomofonMarshalling._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import domofon.tck.entities.GetContact
import spray.json._

trait GetContactItemTest extends BaseTckTest {

  describe("GET /contacts/{id}") {

    it("When contact doesn't exist 404 is returned") {
      Get(s"/contacts/${nonExistentUuid}") ~~> {
        status shouldBe StatusCodes.NotFound
      }
    }

    it("Created Contact could be retrieved") {
      val uuid = postContactRequest().id

      Get(s"/contacts/${uuid}") ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
      }
    }

    it("Returned object is JSON object") {
      val uuid = postContactRequest().id

      Get(s"/contacts/${uuid}") ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
        responseAs[JsValue] shouldBe a[JsObject]
      }
    }

    it("Returned object is JSON object and could be decoded as Contact response") {
      val uuid  = postContactRequest().id

      val request: HttpRequest = Get(s"/contacts/${uuid}") ~> acceptJson

      Get(s"/contacts/${uuid}") ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
        responseAs[GetContact] shouldBe a[GetContact]
      }
    }

    it("When contact was posted without adminEmail, notifyEmail is used instead") {
      val notifyEmail = "some@domain.pl"
      val uuid = postContactRequest(contactRequest().copy(notifyEmail = notifyEmail, adminEmail = None)).id

      Get(s"/contacts/${uuid}") ~~> {
        status shouldBe StatusCodes.OK
        responseAs[GetContact].adminEmail shouldBe notifyEmail
      }
    }

    it("When contact was posted with adminEmail, it is available in GET") {
      val notifyEmail = "some@domain.pl"
      val adminEmail = "admin@domain.pl"
      val uuid = postContactRequest(contactRequest().copy(notifyEmail = notifyEmail, adminEmail = Some(adminEmail))).id

      Get(s"/contacts/${uuid}") ~~> {
        status shouldBe StatusCodes.OK
        responseAs[GetContact].adminEmail shouldBe adminEmail
      }
    }

    it("Doesn't contain message as it might be sensitive information") {
      val uuid = postContactRequest().id

      Get(s"/contacts/${uuid}") ~~> {
        status shouldBe StatusCodes.OK
        val resp = responseAs[JsValue]
        resp shouldBe a[JsObject]

        resp.asJsObject.fields.keys should not contain ("message")
      }
    }

  }
}
