package domofon.tck

import DomofonMarshalling._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import domofon.tck.entities._
import org.scalatest.prop.TableDrivenPropertyChecks._
import spray.json._

trait ChangeContactIsImportantTest extends BaseTckTest {

  private[this] def isImportantUrl(contactId: EntityID): String = {
    s"/contacts/${contactId}/important"
  }

  describe("GET /contacts/{id}/important") {

    it("When contact doesn't exist it also returns 404") {
      Get(isImportantUrl(nonExistentUuid)) ~> acceptJson ~~> {
        status shouldBe StatusCodes.NotFound
      }
    }

    it("By default Contact is not important") {
      val EntityCreatedWithSecret(uuid, _) = postContactRequest()

      Get(isImportantUrl(uuid)) ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
        responseAs[IsImportant].isImportant shouldBe false
      }
    }

    it("By default Contact is not important in GET /contacts/{id}/") {
      val EntityCreatedWithSecret(uuid, _) = postContactRequest()

      Get(s"/contacts/${uuid}") ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
        responseAs[GetContact].isImportant shouldBe false
      }
    }

    it("Can respond with text/plain returning true or false") {
      val EntityCreatedWithSecret(uuid, _) = postContactRequest()

      Get(isImportantUrl(uuid)) ~> acceptPlain ~~> {
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe "false"
      }
    }

  }

  describe("PUT /contacts/{id}/important") {

    it("When contact doesn't exist it is impossible to change importance") {
      Put(isImportantUrl(nonExistentUuid), IsImportant(false).toJson) ~> authorizeWithSecret(nonExistentUuid) ~> acceptJson ~~> {
        status shouldBe StatusCodes.NotFound
      }
    }

    it("It is possible to set importance with PUT /contacts/{id}/important") {
      val EntityCreatedWithSecret(uuid, secret) = postContactRequest()

      Put(isImportantUrl(uuid), IsImportant(true).toJson) ~> acceptJson ~> authorizeWithSecret(secret) ~~> {
        status shouldBe StatusCodes.OK
      }

      Get(isImportantUrl(uuid)) ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
        responseAs[IsImportant].isImportant shouldBe true
      }
    }

    it("It is possible to change importance back to false with PUT /contacts/{id}/important") {
      val EntityCreatedWithSecret(uuid, secret) = postContactRequest()

      Put(isImportantUrl(uuid), IsImportant(true).toJson) ~> acceptJson ~> authorizeWithSecret(secret) ~~> {
        status shouldBe StatusCodes.OK
      }

      Get(isImportantUrl(uuid)) ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
        responseAs[IsImportant].isImportant shouldBe true
      }

      Put(isImportantUrl(uuid), IsImportant(false).toJson) ~> authorizeWithSecret(secret) ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
      }

      Get(isImportantUrl(uuid)) ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
        responseAs[IsImportant].isImportant shouldBe false
      }

    }

    it("Changed importance is reflected in GET /contacts/{id}") {
      val EntityCreatedWithSecret(uuid, secret) = postContactRequest()

      Put(isImportantUrl(uuid), IsImportant(true).toJson) ~> acceptJson ~> authorizeWithSecret(secret) ~~> {
        status shouldBe StatusCodes.OK
      }

      Get(s"/contacts/${uuid}") ~~> {
        status shouldBe StatusCodes.OK
        responseAs[GetContact].isImportant shouldBe true
      }

    }

    it("Discards request if 'isImportant' parameter is missing") {
      val EntityCreatedWithSecret(uuid, secret) = postContactRequest()

      Put(isImportantUrl(uuid), JsObject.empty.toJson) ~> authorizeWithSecret(secret) ~~> {
        status shouldBe StatusCodes.UnprocessableEntity
      }
    }

    it("Discards request if 'isImportant' parameter has wrong type") {
      val EntityCreatedWithSecret(uuid, secret) = postContactRequest()

      val illegalBooleanValues = Table(
        "isImportant",
        JsObject.empty,
        JsString("true"),
        JsString("false"),
        JsString(""),
        JsNumber(0)
      )
      forAll(illegalBooleanValues) { value =>
        val illegalRequestBody = JsObject(("isImportant", value)).toJson
        Put(isImportantUrl(uuid), illegalRequestBody) ~> authorizeWithSecret(secret) ~~> {
          status shouldBe StatusCodes.UnprocessableEntity
        }
      }
    }
  }

}
