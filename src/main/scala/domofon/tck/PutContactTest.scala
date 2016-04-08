package domofon.tck

import akka.http.scaladsl.model.StatusCodes
import akka.util.ByteString

trait PutContactTest extends BaseTckTest {

  describe("PUT /contacts") {
    it("Discards empty body") {
      Post("/contacts") ~> domofonRoute ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }

    it("Discards not valid body") {
      Post("/contacts", ByteString("{}")) ~> domofonRoute ~> check {
        status shouldBe StatusCodes.UnprocessableEntity
      }
    }

  }

}
