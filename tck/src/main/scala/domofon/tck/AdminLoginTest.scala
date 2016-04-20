package domofon.tck
import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.{BasicHttpCredentials, OAuth2BearerToken}

trait AdminLoginTest extends BaseTckTest {
  val (login, password) = ("admin", "Z1ON0101") // todo read from env/config

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
      Get("/login") ~> addCredentials(BasicHttpCredentials(login, password)) ~~> {
        status should equal(StatusCodes.OK)
      }
    }
  }

  describe("GET /logout") {
    it("should respond with Unauthorized for missing admin token") {
      Get("/logout") ~~> {
        status should equal(StatusCodes.Unauthorized)
      }
    }
    it("should respond with Unauthorized for wrong admin token") {
      Get("/logout") ~> addCredentials(OAuth2BearerToken(nonExistentUuid.toString)) ~~> {
        status should equal(StatusCodes.Unauthorized)
      }
    }
    it("should invalidate session for valid admin token") {
      var adminToken = nonExistentUuid.toString
      Get("/login") ~> addCredentials(BasicHttpCredentials(login, password)) ~~> {
        status should equal(StatusCodes.OK)
        adminToken = responseAs[String]
      }

      Get("/logout") ~> addCredentials(OAuth2BearerToken(adminToken)) ~~> {
        status should equal(StatusCodes.OK)
      }

      Get("/logout") ~> addCredentials(OAuth2BearerToken(adminToken)) ~~> {
        status should equal(StatusCodes.Unauthorized)
      }
    }
  }
}