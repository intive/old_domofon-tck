package domofon.tck

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import domofon.tck.DomofonMarshalling._
import domofon.tck.entities.{ValidationFieldsError, PostContactResponse}
import spray.json._

trait PostContactTest extends BaseTckTest {

  describe("POST /contacts") {

    it("Discards wrong request type body") {
      Post("/contacts", "") ~> domofonRoute ~> check {
        status shouldBe StatusCodes.UnsupportedMediaType
      }
    }

    it("Discards request as Json array") {
      Post("/contacts", JsArray()) ~> domofonRoute ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }

    it("Discards empty json object") {
      Post("/contacts", JsObject()) ~> domofonRoute ~> check {
        status shouldBe StatusCodes.UnprocessableEntity
      }
    }

    it("Accepts proper Contact entity, returns text/plain UUID") {
      Post("/contacts", contactRequest().toJson) ~> acceptPlain ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[UUID] shouldBe a[UUID]
      }
    }

    it("Accepts proper Contact entity, returns application/json with UUID") {
      Post("/contacts", contactRequest().toJson) ~> acceptJson ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[PostContactResponse].id should not be empty
      }
    }

    val requiredFields = Set("name", "notifyEmail", "phone")
    for (field <- requiredFields) {
      val cr = contactRequest()
      val json = JsObject(cr.toJson.asJsObject.fields - field)
      it(s"Fails when required field '${field}' is missing as application/json") {
        Post("/contacts", json) ~> acceptJson ~> domofonRoute ~> check {
          status shouldBe StatusCodes.UnprocessableEntity
          responseAs[ValidationFieldsError].fields should contain(field)
        }
      }
    }

    it(s"When failing it notifies about all missing fields as application/json") {
      val cr = contactRequest()
      val json = JsObject(cr.toJson.asJsObject.fields -- requiredFields)
      Post("/contacts", json) ~> acceptJson ~> domofonRoute ~> check {
        status shouldBe StatusCodes.UnprocessableEntity
        val r = requiredFields -- responseAs[ValidationFieldsError].fields
        r shouldBe empty
      }
    }

    for (field <- requiredFields) {
      val cr = contactRequest()
      val json = JsObject(cr.toJson.asJsObject.fields - field)
      it(s"Fails when required field '${field}' is missing as text/plain") {
        Post("/contacts", json) ~> acceptPlain ~> domofonRoute ~> check {
          status shouldBe StatusCodes.UnprocessableEntity
          responseAs[String] should include(field)
        }
      }
    }

    it(s"When failing it notifies about all missing fields text/plain") {
      val cr = contactRequest()
      val json = JsObject(cr.toJson.asJsObject.fields -- requiredFields)
      Post("/contacts", json) ~> acceptPlain ~> domofonRoute ~> check {
        status shouldBe StatusCodes.UnprocessableEntity
        val r = responseAs[String]
        requiredFields.foreach {
          field =>
            r should include(field)
        }
      }
    }

  }
}
