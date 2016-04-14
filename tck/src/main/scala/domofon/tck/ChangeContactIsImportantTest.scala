package domofon.tck

import java.util.UUID

import DomofonMarshalling._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import domofon.tck.entities.{GetContact, IsImportant, Deputy}
import spray.json._

trait ChangeContactIsImportantTest extends BaseTckTest {

  private[this] def isImportantUrl(contactId: UUID): String = {
    s"/contacts/${contactId}/important"
  }

  describe("GET /contacts/{id}/important") {

    it("When contact doesn't exist it also returns 404") {
      Get(isImportantUrl(nonExistentUuid)) ~> acceptJson ~> domofonRoute ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    it("By default Contact is not important") {
      val uuid = postContactRequest()

      Get(isImportantUrl(uuid)) ~> acceptJson ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[IsImportant].isImportant shouldBe false
      }
    }

    it("By default Contact is not important in GET /contacts/{id}/") {
      val uuid = postContactRequest()

      Get(s"/contacts/${uuid}") ~> acceptJson ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[GetContact].isImportant shouldBe Some(false)
      }
    }

  }

  describe("PUT /contacts/{id}/important") {

    it("When contact doesn't exist it is impossible to change importance") {
      Put(isImportantUrl(nonExistentUuid), IsImportant(false).toJson) ~> domofonRoute ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    it("It is possible to set importance with PUT /contacts/{id}/important") {
      val uuid = postContactRequest()

      Put(isImportantUrl(uuid), IsImportant(true).toJson) ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
      }

      Get(isImportantUrl(uuid)) ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[IsImportant].isImportant shouldBe true
      }
    }

    it("It is possible to change importance back to falsewith PUT /contacts/{id}/important") {
      val uuid = postContactRequest()

      Put(isImportantUrl(uuid), IsImportant(true).toJson) ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
      }

      Get(isImportantUrl(uuid)) ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[IsImportant].isImportant shouldBe true
      }

      Put(isImportantUrl(uuid), IsImportant(false).toJson) ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
      }

      Get(isImportantUrl(uuid)) ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[IsImportant].isImportant shouldBe false
      }

    }

    it("Changed importance is reflected in GET /contacts/{id}") {
      val uuid = postContactRequest()

      Put(isImportantUrl(uuid), IsImportant(true).toJson) ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
      }

      Get(s"/contacts/${uuid}") ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[GetContact].isImportant shouldBe Some(true)
      }

    }

    it("Can respond only with application/json and fails with other response types on PUT /contacts/{id}/important when exists") {
      val uuid = postContactRequest()

      Get(isImportantUrl(uuid)) ~> acceptPlain ~> domofonRoute ~> check {
        status shouldBe StatusCodes.NotAcceptable
      }
    }
  }

}
