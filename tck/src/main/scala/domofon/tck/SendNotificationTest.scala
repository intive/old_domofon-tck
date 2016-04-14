package domofon.tck

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import domofon.tck.DomofonMarshalling._
import domofon.tck.entities.{NotificationRetry, PostContactResponse, ValidationFieldsError}
import spray.json._

trait SendNotificationTest extends BaseTckTest {

  private[this] def notifyUrl(contactId: UUID): String = {
    s"/contacts/${contactId}/notify"
  }

  describe("POST /contacts/{id}/notify") {
    it("When contact doesn't exist it is impossible to send notification") {
      Post(notifyUrl(nonExistentUuid)) ~> domofonRoute ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    it("It sends notification if it exists") {
      val uuid = postContactRequest()
      Post(notifyUrl(uuid)) ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
      }
    }

    it("It discards notifications happening too often") {
      val uuid = postContactRequest()
      Post(notifyUrl(uuid)) ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
      }

      Post(notifyUrl(uuid)) ~> domofonRoute ~> check {
        status shouldBe StatusCodes.TooManyRequests
      }
    }

    it("It tells when it is possible to retry sending notification as application/json") {
      val uuid = postContactRequest()
      Post(notifyUrl(uuid)) ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
      }

      Post(notifyUrl(uuid)) ~> acceptJson ~> domofonRoute ~> check {
        status shouldBe StatusCodes.TooManyRequests
        responseAs[NotificationRetry].message should not be empty
      }
    }

  }
}
