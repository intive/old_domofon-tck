package domofon.tck

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import domofon.tck.entities.{ContactResponse, ContactCreateResponse, ContactRequest}
import spray.json._

trait GetContactItemTest extends BaseTckTest with Marshalling with SprayJsonSupport {

  describe("GET /contacts/{id}") {

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

    it("Returned object is JSON object and could be decoded as ContactResponse") {
      val uuid = postContactRequest()

      Get(s"/contacts/${uuid}") ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[ContactResponse] shouldBe a[ContactResponse]
      }
    }
  }
}
