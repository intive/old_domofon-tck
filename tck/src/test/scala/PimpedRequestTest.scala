import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import domofon.tck.BaseTckTest

class PimpedRequestTest extends BaseTckTest {
  override def domofonRoute: Route = Route.seal(reject())

  describe("PimpedRequest") {
    it("Should print detailed error in case of failure (pending is OK)") {
      pendingUntilFixed {
        Get("/") ~~> {
          status shouldBe StatusCodes.OK
        }
      }
    }
  }

  override def tckAdminLogin: String = fail("tckAdminLogin shouldn't be used in this test")
  override def tckAdminPass: String = fail("tckAdminPass shouldn't be used in this test")
}
