package domofon.tck

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import spray.json._

trait ContatctMessageTest extends BaseTckTest {
  describe("PUT /contact/{id}/message") {
    it("should fail with 404 for non-existent contact") {
      Put(s"/contacts/${UUID.randomUUID()}/message").withEntity("Got a package for ya!") ~> domofonRoute ~> check {
        status should equal(StatusCodes.NotFound)
      }
    }

    it("should update message for existing contact, respond with String") {
      val uuid = postContactRequest()
      Put(s"/contacts/$uuid/message").withEntity("Got a package for ya!") ~> acceptPlain ~> domofonRoute ~> check {
        status should equal(StatusCodes.OK)
        responseAs[String] should equal("OK")
      }
    }

    it("should update message for existing contact, respond with JSON") {
      val uuid = postContactRequest()
      Put(s"/contacts/$uuid/message").withEntity("Got a package for ya!") ~> acceptJson ~> domofonRoute ~> check {
        status should equal(StatusCodes.OK)
        responseAs[String].parseJson.asJsObject.fields("status") should equal(JsString("OK"))
      }
    }
  }
}
