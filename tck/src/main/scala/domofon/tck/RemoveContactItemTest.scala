package domofon.tck

import akka.http.scaladsl.model.StatusCodes

trait RemoveContactItemTest extends BaseTckTest {

  describe("DELETE /contacts/{id}") {

    it("When contact doesn't exist 404 is returned") {

      Delete(s"/contacts/$nonExistentUuid") ~~> {
        status shouldBe StatusCodes.NotFound
      }
    }

    it("Remove contact when one exists") {
      val uuid = postContactRequest()

      Delete(s"/contacts/$uuid") ~~> {
        status shouldBe StatusCodes.OK
      }
    }
  }
}

