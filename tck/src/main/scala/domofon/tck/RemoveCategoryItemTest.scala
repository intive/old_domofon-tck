package domofon.tck

import akka.http.scaladsl.model.StatusCodes

trait RemoveCategoryItemTest extends BaseTckTest {

  describe("DELETE /categories/{id}") {

    it("When category doesn't exist 404 is returned") {

      Delete(s"/categories/$nonExistentUuid") ~~> {
        status shouldBe StatusCodes.NotFound
      }
    }

    it("Remove category when one exists") {
      val uuid = postCategoryRequest().id
      Delete(s"/categories/$uuid") ~> authorizeWithSecret(loginAdmin) ~~> {
        status shouldBe StatusCodes.OK
      }
    }
  }
}

