package domofon.tck

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.{BasicHttpCredentials, OAuth2BearerToken}

trait AdminLoginTest extends BaseTckTest with AdminLogin {

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
      Get("/login") ~> addCredentials(BasicHttpCredentials(tckAdminLogin, tckAdminPass)) ~~> {
        status should equal(StatusCodes.OK)
      }
    }
  }

}

trait AdminLogin extends AdminCredentials { self: BaseTckTest =>
  type AdminToken = String

  def loginAdmin: AdminToken = {
    var adminToken = nonExistentUuid.toString
    Get("/login") ~> addCredentials(BasicHttpCredentials(tckAdminLogin, tckAdminPass)) ~~> {
      status should equal(StatusCodes.OK)
      adminToken = responseAs[AdminToken]
    }
    adminToken
  }
}

trait AdminCredentials {
  def tckAdminLogin: String
  def tckAdminPass: String
}

trait TckEnvCredentials extends AdminCredentials {
  import TckEnvCredentials._

  override val tckAdminLogin: String = sys.env.getOrElse(AdminLoginEnvName, "admin")
  override val tckAdminPass: String = sys.env.getOrElse(AdminPasswordEnvName, "P4ssw0rd")
}

object TckEnvCredentials {
  val AdminLoginEnvName = "TCK_ADMIN_LOGIN"
  val AdminPasswordEnvName = "TCK_ADMIN_PASSWORD"
}

