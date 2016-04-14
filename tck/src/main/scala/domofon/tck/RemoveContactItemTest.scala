package domofon.tck

import akka.http.scaladsl.model.StatusCodes

trait RemoveContactItemTest extends BaseTckTest {

  describe("DELETE /contacts/{id}") {

    it("When contact doesn't exist 404 is returned") {

      Delete(s"/contacts/$nonExistentUuid") ~> domofonRoute ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    it("Remove contact when one exists") {
      val uuid = postContactRequest()

      Delete(s"/contacts/$uuid") ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }
}

