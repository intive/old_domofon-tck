package domofon.tck

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import domofon.tck.DomofonMarshalling._
import domofon.tck.entities.GetContact
import spray.json.{JsArray, JsObject}

trait DomofonYamlTest extends BaseTckTest {

  describe("GET /domofon.yaml") {

    it("Server should provide API endpoint for simplicyty of Swagger-UI usage") {
      Get("/domofon.yaml") ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
        responseEntity.contentType shouldBe ContentTypes.`text/plain(UTF-8)`
      }
    }
  }
}
