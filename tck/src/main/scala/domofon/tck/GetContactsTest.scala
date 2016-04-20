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

  }
}
