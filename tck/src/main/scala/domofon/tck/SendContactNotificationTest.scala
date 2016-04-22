package domofon.tck


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import domofon.tck.DomofonMarshalling._
import domofon.tck.entities.{EntityID, NotificationRetry}
import spray.json._

trait SendContactNotificationTest extends BaseTckTest {

  private[this] def notifyUrl(contactId: EntityID): String = {
    s"/contacts/${contactId}/notify"
  }

  describe("POST /contacts/{id}/notify") {
    it("When contact doesn't exist it is impossible to send notification") {
      Post(notifyUrl(nonExistentUuid)) ~~> {
        status shouldBe StatusCodes.NotFound
      }
    }

    it("It sends notification if it exists") {
      val uuid = postContactRequest().id
      Post(notifyUrl(uuid)) ~~> {
        status shouldBe StatusCodes.OK
      }
    }

    it("It discards notifications happening too often") {
      val uuid = postContactRequest().id
      Post(notifyUrl(uuid)) ~~> {
        status shouldBe StatusCodes.OK
      }

      Post(notifyUrl(uuid)) ~~> {
        status shouldBe StatusCodes.TooManyRequests
      }
    }

    it("It tells when it is possible to retry sending notification as application/json") {
      val uuid = postContactRequest().id
      Post(notifyUrl(uuid)) ~~> {
        status shouldBe StatusCodes.OK
      }

      Post(notifyUrl(uuid)) ~> acceptJson ~~> {
        status shouldBe StatusCodes.TooManyRequests
        responseAs[NotificationRetry].message should not be empty
      }
    }

  }
}
