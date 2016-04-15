package domofon.tck

import akka.http.scaladsl.model.{ContentTypes, StatusCodes}

trait DomofonYamlTest extends BaseTckTest {

  describe("GET /domofon.yaml") {

    it("Server should provide API endpoint for simplicity of Swagger-UI usage") {
      Get("/domofon.yaml") ~~> {
        status shouldBe StatusCodes.OK
        responseEntity.contentType shouldBe ContentTypes.`text/plain(UTF-8)`
      }
    }
  }
}
