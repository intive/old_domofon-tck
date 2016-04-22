package domofon.tck

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import domofon.tck.DomofonMarshalling._
import domofon.tck.entities.GetCategory
import spray.json.{JsArray, JsObject}
import scala.collection.breakOut

trait GetCategoriesTest extends BaseTckTest {

  private[this] val categoriesEndpoint = "/categories"

  describe("GET /categories") {

    it("Responds with OK status on GET") {
      Get(categoriesEndpoint) ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
      }
    }

    it("Can respond only with application/json and fails with other response types") {
      Get(categoriesEndpoint) ~> acceptPlain ~~> {
        status shouldBe StatusCodes.NotAcceptable
      }
    }

    it("Response is JSON array") {
      Get(categoriesEndpoint) ~> acceptJson ~~> {
        responseAs[JsArray] shouldBe a[JsArray]
      }
    }

    it("If there are returned items, they are valid JSON objects") {
      Get(categoriesEndpoint) ~> acceptJson ~~> {
        responseAs[List[JsObject]] shouldBe a[List[_]]
      }
    }

    it("Created Category could be listed") {
      val uuid: UUID = postCategoryRequest().id

      Get(categoriesEndpoint) ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
        val categories = responseAs[List[GetCategory]]
        categories.filter(_.id === uuid) should have size (1)
      }
    }

    it("Created Category has notification message") {
      val message = "Some notification message"
      val uuid: UUID = postCategoryRequest(categoryRequest(message = message)).id

      Get(categoriesEndpoint) ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
        val categories = responseAs[List[GetCategory]]
        categories.filter(_.id === uuid) should have size (1)
        val messages = categories.filter(_.id === uuid).flatMap(_.messages.values)
        messages should contain(message)
      }
    }

  }
}
