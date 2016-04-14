package domofon.tck

import akka.http.scaladsl.model.StatusCodes
import spray.json._

trait ContactMessageTest extends BaseTckTest {

  describe("GET /contact/{id}/message") {
    it("should fail with 404 for non-existent contact") {
      Get(s"/contacts/${nonExistentUuid}/message") ~> domofonRoute ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    it("Should return default message") {
      val uuid = postContactRequest()
      Get(s"/contacts/${uuid}/message") ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
      }
    }

  }

  describe("PUT /contact/{id}/message") {
    it("should fail with 404 for non-existent contact") {
      Put(s"/contacts/${nonExistentUuid}/message").withEntity("Got a package for ya!") ~> domofonRoute ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    it("should update message for existing contact, respond with String") {
      val uuid = postContactRequest()
      Put(s"/contacts/$uuid/message").withEntity("Got a package for ya!") ~> acceptPlain ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] should equal("OK")
      }
    }

    it("should update message for existing contact, respond with JSON") {
      val uuid = postContactRequest()
      Put(s"/contacts/$uuid/message").withEntity("Got a package for ya!") ~> acceptJson ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String].parseJson.asJsObject.fields("status") should equal(JsString("OK"))
      }
    }

    it("Updated message could be retrieved") {
      val uuid = postContactRequest()
      val message = "Got a package for ya!"
      Put(s"/contacts/$uuid/message").withEntity(message) ~> acceptJson ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String].parseJson.asJsObject.fields("status") should equal(JsString("OK"))
      }

      Get(s"/contacts/${uuid}/message") ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe message
      }
    }

  }
}
