package domofon.tck

import domofon.tck.DomofonMarshalling._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import domofon.tck.entities.GetContact
import spray.json._

trait GetContactItemTest extends BaseTckTest {

  describe("GET /contacts/{id}") {

    it("When contact doesn't exist 404 is returned") {
      Get(s"/contacts/${nonExistentUuid}") ~> domofonRoute ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    it("Requesting ID not in UUID format fails") {
      val notUuid = "this-is-not-uuid"

      Get(s"/contacts/${notUuid}") ~> domofonRoute ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }

    it("Created Contact could be retrieved") {
      val uuid = postContactRequest()

      Get(s"/contacts/${uuid}") ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
      }
    }

    it("Returned object is JSON object") {
      val uuid = postContactRequest()

      Get(s"/contacts/${uuid}") ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[JsValue] shouldBe a[JsObject]
      }
    }

    it("Returned object is JSON object and could be decoded as Contact response") {
      val uuid = postContactRequest()

      Get(s"/contacts/${uuid}") ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[GetContact] shouldBe a[GetContact]
      }
    }

    it("When contact was posted without adminEmail, notifyEmail is used instead") {
      val notifyEmail = "some@domain.pl"
      val uuid = postContactRequest(contactRequest().copy(notifyEmail = notifyEmail, adminEmail = None))

      Get(s"/contacts/${uuid}") ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[GetContact].adminEmail shouldBe notifyEmail
      }
    }

  }
}
