package domofon.tck

import java.util.UUID
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import domofon.tck.DomofonMarshalling._
import domofon.tck.entities.NotificationRetry
import spray.json._
import akka.http.scaladsl.model.StatusCodes
import domofon.tck.entities.NotificationRetry

trait SendCategoryNotificationTest extends BaseTckTest {

  private[this] def notifyUrl(categoryId: UUID): String = {
    s"/categories/${categoryId}/notify"
  }

  private[this] def batchCategoryRequest = categoryRequest(isBatch = true)

  describe("POST /categories/{id}/notify") {
    it("When category doesn't exist it is impossible to send notification") {
      Post(notifyUrl(nonExistentUuid)) ~~> {
        status shouldBe StatusCodes.NotFound
      }
    }

    it("It sends notification if it exists and isBatch") {
      val uuid: UUID = postCategoryRequest(batchCategoryRequest).id
      Post(notifyUrl(uuid)) ~~> {
        status shouldBe StatusCodes.OK
      }
    }

    it("It fails with BadRequest when it exists and is not isBatch") {
      val uuid: UUID = postCategoryRequest(categoryRequest(isBatch = false)).id
      Post(notifyUrl(uuid)) ~~> {
        status shouldBe StatusCodes.BadRequest
      }
    }

    it("It discards notifications happening too often") {
      val uuid: UUID = postCategoryRequest(batchCategoryRequest).id
      Post(notifyUrl(uuid)) ~~> {
        status shouldBe StatusCodes.OK
      }

      Post(notifyUrl(uuid)) ~~> {
        status shouldBe StatusCodes.TooManyRequests
      }
    }

    it("It tells when it is possible to retry sending notification as application/json") {
      val uuid: UUID = postCategoryRequest(batchCategoryRequest).id
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
