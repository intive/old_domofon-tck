package domofon.tck

import akka.http.scaladsl.model.StatusCodes
import domofon.tck.entities.Deputy
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import domofon.tck.DomofonMarshalling._

trait AdminOpsTest extends BaseTckTest with AdminCredentials {

  describe("Admin operations") {
    it("DELETE /contacts/{id} should be possible for admin to execute") {
      val token = loginAdmin
      val id = postContactRequest().id

      Delete(s"/contacts/${id}") ~> authorizeWithSecret(token) ~~> {
        status should equal(StatusCodes.OK)
      }
    }
    it("DELETE /contacts/{id}/deputy should be possible for admin to execute") {
      val token = loginAdmin
      val id = postContactRequest().id

      Delete(s"/contacts/${id}/deputy") ~> authorizeWithSecret(token) ~~> {
        status should equal(StatusCodes.OK)
      }
    }
    it("PUT /contacts/{id}/deputy should be possible for admin to execute") {
      val token = loginAdmin
      val id = postContactRequest().id

      Put(s"/contacts/${id}/deputy", Deputy("Jan Kowalski", "jan.kowalski@company.pl", "+48123321123").toJson) ~> authorizeWithSecret(token) ~~> {
        status should equal(StatusCodes.OK)
      }
    }

  }

}
