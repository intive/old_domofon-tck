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

    it("Returned swagger definition should contain host:") {
      Get("/domofon.yaml") ~~> {
        val lines = entityAs[String].split("[\\r\\n]+")

        lines.count(_.trim.startsWith("host:")) should not be (0)

      }
    }

  }
}
