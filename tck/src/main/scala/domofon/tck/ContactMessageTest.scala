package domofon.tck

import akka.http.scaladsl.model.StatusCodes
import domofon.tck.BaseTckTest.ContactCreationResult
import spray.json._

trait ContactMessageTest extends BaseTckTest {

  describe("GET /contact/{id}/message") {
    it("should fail with 404 for non-existent contact") {
      Get(s"/contacts/${nonExistentUuid}/message") ~~> {
        status shouldBe StatusCodes.NotFound
      }
    }

    it("Should return default message") {
      val uuid = postContactRequest().id
      Get(s"/contacts/${uuid}/message") ~~> {
        status shouldBe StatusCodes.OK
      }
    }

  }

  describe("PUT /contact/{id}/message") {
    it("should fail with 404 for non-existent contact") {
      Put(s"/contacts/${nonExistentUuid}/message").withEntity("Got a package for ya!") ~> authorizeWithSecret(nonExistentUuid) ~~> {
        status shouldBe StatusCodes.NotFound
      }
    }

    it("should update message for existing contact, respond with String") {
      val ContactCreationResult(uuid, secret) = postContactRequest()
      Put(s"/contacts/$uuid/message").withEntity("Got a package for ya!") ~> authorizeWithSecret(secret) ~> acceptPlain ~~> {
        status shouldBe StatusCodes.OK
        responseAs[String] should equal("OK")
      }
    }

    it("should update message for existing contact, respond with JSON") {
      val ContactCreationResult(uuid, secret) = postContactRequest()
      Put(s"/contacts/$uuid/message").withEntity("Got a package for ya!") ~> authorizeWithSecret(secret) ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
        responseAs[String].parseJson.asJsObject.fields("status") should equal(JsString("OK"))
      }
    }

    it("Updated message could be retrieved") {
      val ContactCreationResult(uuid, secret) = postContactRequest()
      val message = "Got a package for ya!"
      Put(s"/contacts/$uuid/message").withEntity(message) ~> authorizeWithSecret(secret) ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
        responseAs[String].parseJson.asJsObject.fields("status") should equal(JsString("OK"))
      }

      Get(s"/contacts/${uuid}/message") ~~> {
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe message
      }
    }

  }
}
