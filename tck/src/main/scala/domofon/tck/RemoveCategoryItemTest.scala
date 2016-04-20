package domofon.tck

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes

trait RemoveCategoryItemTest extends BaseTckTest {

  describe("DELETE /categories/{id}") {

    it("When cateogry doesn't exist 404 is returned") {

      Delete(s"/categories/$nonExistentUuid") ~~> {
        status shouldBe StatusCodes.NotFound
      }
    }

    it("Remove category when one exists") {
      val uuid: UUID = postCategoryRequest().id

      Delete(s"/categories/$uuid") ~~> {
        status shouldBe StatusCodes.OK
      }
    }
  }
}

