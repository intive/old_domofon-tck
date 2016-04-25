package domofon.tck

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import domofon.tck.entities.GetCategory
import domofon.tck.DomofonMarshalling._
import spray.json._

trait GetCategoryItemTest extends BaseTckTest {

  describe("GET /categories/{id}") {

    it("When category doesn't exist 404 is returned") {
      Get(s"/contacts/${nonExistentUuid}") ~~> {
        status shouldBe StatusCodes.NotFound
      }
    }

    it("Created Category could be retrieved") {
      val uuid = postCategoryRequest().id

      Get(s"/categories/${uuid}") ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
      }
    }

    it("Returned object is JSON object") {
      val uuid = postCategoryRequest().id

      Get(s"/categories/${uuid}") ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
        responseAs[JsValue] shouldBe a[JsObject]
      }
    }

    it("Returned object is JSON object and could be decoded as Contact response") {
      val uuid = postCategoryRequest().id

      Get(s"/categories/${uuid}") ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
        responseAs[GetCategory] shouldBe a[GetCategory]
      }
    }

  }
}
