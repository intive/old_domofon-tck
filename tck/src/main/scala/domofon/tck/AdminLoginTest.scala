package domofon.tck

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.{BasicHttpCredentials, OAuth2BearerToken}

trait AdminLoginTest extends BaseTckTest with AdminCredentials {

  describe("GET /login") {
    it("should respond with Unauthorized for missing credentials") {
      Get("/login") ~~> {
        status should equal(StatusCodes.Unauthorized)
      }
    }
    it("should respond with Unauthorized for wrong credentials") {
      Get("/login") ~> addCredentials(BasicHttpCredentials("cypher", "ignorance is bliss")) ~~> {
        status should equal(StatusCodes.Unauthorized)
      }
    }
    it("should respond with a token of an admin session for correct credentials via Basic") {
      Get("/login") ~> addCredentials(BasicHttpCredentials(AdminLogin, AdminPass)) ~~> {
        status should equal(StatusCodes.OK)
      }
    }
  }

}

trait AdminCredentials { self: BaseTckTest =>
  val AdminLogin = "admin"
  val AdminPass = "Z1ON0101" // todo read from env/config
  type AdminToken = String

  def loginAdmin: AdminToken = {
    var adminToken = nonExistentUuid.toString
    Get("/login") ~> addCredentials(BasicHttpCredentials(AdminLogin, AdminPass)) ~~> {
      status should equal(StatusCodes.OK)
      adminToken = responseAs[AdminToken]
    }
    adminToken
  }
}
