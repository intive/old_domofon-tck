package domofon.tck

import java.time.LocalDate
import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import domofon.tck.DomofonMarshalling._
import domofon.tck.entities.{EntityCreatedWithSecret, EntityCreated, ValidationFieldsError}
import org.scalatest.prop.TableDrivenPropertyChecks._
import spray.json._

trait PostContactTest extends BaseTckTest {

  describe("POST /contacts") {

    it("Discards wrong request type body") {
      Post("/contacts", "") ~~> {
        status shouldBe StatusCodes.UnsupportedMediaType
      }
    }

    it("Discards request as Json array") {
      Post("/contacts", JsArray()) ~~> {
        status shouldBe StatusCodes.BadRequest
      }
    }

    it("Discards empty json object") {
      Post("/contacts", JsObject()) ~~> {
        status shouldBe StatusCodes.UnprocessableEntity
      }
    }

    it("Discards json with illegal string values") {
      val validContactRequest = contactRequest().toJson.asJsObject
      val stringFields = Seq("name", "company", "notifyEmail", "phone", "adminEmail")
      val illegalStringValues = Seq(JsNumber(42), JsString(""), JsBoolean(false), JsObject.empty)
      val cartesianProduct = for {
        field <- stringFields
        value <- illegalStringValues
      } yield (field, value)
      val invalidParameters = Table(("field", "value"), cartesianProduct: _*)
      forAll(invalidParameters) { (field, value) =>
        val invalidContactRequest = JsObject(validContactRequest.fields.updated(field, value))
        Post("/contacts", invalidContactRequest.toJson) ~~> {
          status shouldBe StatusCodes.UnprocessableEntity
        }
      }
    }

    it("Discards json with illegal date values") {
      import DomofonMarshalling._

      val validContactRequest = contactRequest().toJson.asJsObject
      val values = Seq(
        (None, Some(LocalDate.now().toString)),
        (Some(LocalDate.now().toString), None),
        (Some("2012-12-12"), Some("2012-11-11"))
      ).map(x => (x._1.toJson, x._2.toJson)) ++ Seq(
          (JsString("2000.01.1"), JsString("2000-01")),
          (JsString("02/29/2007"), JsNull)
        )

      val dateFields = Table(("fromDate", "tillDate"), values: _*)
      forAll(dateFields) { (from, till) =>
        val invalidContactRequest = JsObject(validContactRequest.fields ++ Map("fromDate" -> from, "tillDate" -> till))
        Post("/contacts", invalidContactRequest.toJson) ~~> {
          status shouldBe StatusCodes.UnprocessableEntity
        }
      }

    }

    it("Rejects Contact with wrong Category UUID as text/plain") {
      Post("/contacts", contactRequest(category = nonExistentUuid).toJson) ~> acceptPlain ~~> {
        status shouldBe StatusCodes.UnprocessableEntity
      }
    }

    it("Rejects Contact with wrong Category UUID as application/json") {
      Post("/contacts", contactRequest(category = nonExistentUuid).toJson) ~> acceptJson ~~> {
        status shouldBe StatusCodes.UnprocessableEntity
      }
    }

    it("Accepts proper Contact entity, returns text/plain UUID") {
      Post("/contacts", contactRequest().toJson) ~> acceptPlain ~~> {
        status shouldBe StatusCodes.OK
        responseAs[UUID] shouldBe a[UUID]
      }
    }

    it("Accepts proper Contact entity, returns application/json with UUID") {
      Post("/contacts", contactRequest().toJson) ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
        responseAs[EntityCreated].id shouldBe a[UUID]
      }
    }
    /*
    it("Accepts proper Contact entity, returns application/json with UUID and secret") {
      Post("/contacts", contactRequest().toJson) ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
        val created = responseAs[EntityCreatedWithSecret]
        created.id shouldBe a[UUID]
        created.secret shouldBe a[UUID]
      }
    }
*/
    val requiredFields = Set("name", "notifyEmail", "phone")
    for (field <- requiredFields) {
      it(s"Fails when required field '${field}' is missing as application/json") {
        val cr = contactRequest()
        val json = JsObject(cr.toJson.asJsObject.fields - field)
        Post("/contacts", json) ~> acceptJson ~~> {
          status shouldBe StatusCodes.UnprocessableEntity
          responseAs[ValidationFieldsError].fields should contain(field)
        }
      }
    }

    it(s"When failing it notifies about all missing fields as application/json") {
      val cr = contactRequest()
      val json = JsObject(cr.toJson.asJsObject.fields -- requiredFields)
      Post("/contacts", json) ~> acceptJson ~~> {
        status shouldBe StatusCodes.UnprocessableEntity
        val r = requiredFields -- responseAs[ValidationFieldsError].fields
        r shouldBe empty
      }
    }

    for (field <- requiredFields) {
      it(s"Fails when required field '${field}' is missing as text/plain") {
        val cr = contactRequest()
        val json = JsObject(cr.toJson.asJsObject.fields - field)
        Post("/contacts", json) ~> acceptPlain ~~> {
          status shouldBe StatusCodes.UnprocessableEntity
          responseAs[String] should include(field)
        }
      }
    }

    it(s"When failing it notifies about all missing fields text/plain") {
      val cr = contactRequest()
      val json = JsObject(cr.toJson.asJsObject.fields -- requiredFields)
      Post("/contacts", json) ~> acceptPlain ~~> {
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
