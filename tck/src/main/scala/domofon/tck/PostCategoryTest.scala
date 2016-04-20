package domofon.tck

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import domofon.tck.DomofonMarshalling._
import domofon.tck.entities.{EntityCreated, ValidationFieldsError}
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.prop.Tables.Table
import spray.json._

trait PostCategoryTest extends BaseTckTest {

  private[this] val categoriesEndpoint = "/categories"

  describe(s"POST /categories") {

    it("Discards wrong request type body") {
      Post(categoriesEndpoint, "") ~~> {
        status shouldBe StatusCodes.UnsupportedMediaType
      }
    }

    it("Discards request as Json array") {
      Post(categoriesEndpoint, JsArray()) ~~> {
        status shouldBe StatusCodes.BadRequest
      }
    }

    it("Discards empty json object") {
      Post(categoriesEndpoint, JsObject()) ~~> {
        status shouldBe StatusCodes.UnprocessableEntity
      }
    }

    it("Accepts proper Category entity, returns text/plain UUID") {
      Post(categoriesEndpoint, categoryRequest().toJson) ~> acceptPlain ~~> {
        status shouldBe StatusCodes.OK
        responseAs[UUID] shouldBe a[UUID]
      }
    }

    it("Accepts proper Category entity, returns application/json with UUID") {
      Post(categoriesEndpoint, categoryRequest().toJson) ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
        responseAs[EntityCreated].id shouldBe a[UUID]
      }
    }

    val requiredFields = Set("name", "description", "isBatch", "message")
    for (field <- requiredFields) {
      val cr = categoryRequest()
      val json = JsObject(cr.toJson.asJsObject.fields - field)
      it(s"Fails when required field '${field}' is missing as application/json") {
        Post(categoriesEndpoint, json) ~> acceptJson ~~> {
          status shouldBe StatusCodes.UnprocessableEntity
          responseAs[ValidationFieldsError].fields should contain(field)
        }
      }
    }

    it(s"When failing it notifies about all missing fields as application/json") {
      val cr = categoryRequest()
      val json = JsObject(cr.toJson.asJsObject.fields -- requiredFields)
      Post(categoriesEndpoint, json) ~> acceptJson ~~> {
        status shouldBe StatusCodes.UnprocessableEntity
        val r = requiredFields -- responseAs[ValidationFieldsError].fields
        r shouldBe empty
      }
    }

    for (field <- requiredFields) {
      val cr = categoryRequest()
      val json = JsObject(cr.toJson.asJsObject.fields - field)
      it(s"Fails when required field '${field}' is missing as text/plain") {
        Post(categoriesEndpoint, json) ~> acceptPlain ~~> {
          status shouldBe StatusCodes.UnprocessableEntity
          responseAs[String] should include(field)
        }
      }
    }

    it(s"When failing it notifies about all missing fields text/plain") {
      val cr = categoryRequest()
      val json = JsObject(cr.toJson.asJsObject.fields -- requiredFields)
      Post(categoriesEndpoint, json) ~> acceptPlain ~~> {
        status shouldBe StatusCodes.UnprocessableEntity
        val r = responseAs[String]
        requiredFields.foreach {
          field =>
            r should include(field)
        }
      }
    }

    it("Discards json with illegal string values") {
      val validCategoryRequest = categoryRequest().toJson.asJsObject
      val stringFields = Seq("name", "description", "message")
      val illegalStringValues = Seq(JsNumber(42), JsString(""), JsBoolean(false), JsObject.empty)
      val cartesianProduct = for {
        field <- stringFields
        value <- illegalStringValues
      } yield (field, value)
      val invalidParameters = Table(("field", "value"), cartesianProduct: _*)
      forAll(invalidParameters) { (field, value) =>
        val invalidContactRequest = JsObject(validCategoryRequest.fields.updated(field, value))
        Post("/categories", invalidContactRequest.toJson) ~~> {
          status shouldBe StatusCodes.UnprocessableEntity
        }
      }
    }

  }
}
