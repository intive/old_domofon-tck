package domofon.tck


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import domofon.tck.DomofonMarshalling._
import domofon.tck.entities.GetContact
import spray.json.{JsArray, JsObject}

trait GetContactsTest extends BaseTckTest {

  describe("GET /contacts") {

    it("Responds with OK status on GET") {
      Get("/contacts") ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
      }
    }

    it("Can respond only with application/json and fails with other response types") {
      Get("/contacts") ~> acceptPlain ~~> {
        status shouldBe StatusCodes.NotAcceptable
      }
    }

    it("Response is JSON array") {
      Get("/contacts") ~> acceptJson ~~> {
        responseAs[JsArray] shouldBe a[JsArray]
      }
    }

    it("If there are returned items, they are valid JSON objects") {
      Get("/contacts") ~> acceptJson ~~> {
        responseAs[List[JsObject]] shouldBe a[List[_]]
      }
    }

    it("Created Contact could be listed") {
      val uuid = postContactRequest().id

      Get(s"/contacts") ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
        val contacts = responseAs[List[GetContact]]
        contacts.filter(_.id === uuid) should have size (1)
      }
    }

    it("Contacts from different categories are filtered out") {
      val categoryId = postCategoryRequest().id
      val otherContactId = postContactRequest().id
      Get(s"/contacts?category=${categoryId}") ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
        val contacts = responseAs[List[GetContact]]
        contacts should have size (0)
      }
    }

    it("Is possible to filter contacts by category") {
      val categoryId = postCategoryRequest().id
      val sameCategoryId = postContactRequest(contactRequest(category = categoryId)).id
      Get(s"/contacts?category=${categoryId}") ~> acceptJson ~~> {
        status shouldBe StatusCodes.OK
        val contacts = responseAs[List[GetContact]]
        contacts.filter(_.id === sameCategoryId) should have size (1)
      }
    }

    it("Filtering by non existent category is BadRequest") {
      val categoryId = nonExistentUuid
      Get(s"/contacts?category=${categoryId}") ~> acceptJson ~~> {
        status shouldBe StatusCodes.BadRequest
      }
    }

  }
}
