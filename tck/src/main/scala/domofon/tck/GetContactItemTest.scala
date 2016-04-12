package domofon.tck

import domofon.tck.DomofonMarshalling._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import domofon.tck.entities.ContactResponse
import spray.json._

trait GetContactItemTest extends BaseTckTest {

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
