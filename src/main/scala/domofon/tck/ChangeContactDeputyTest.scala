package domofon.tck

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import domofon.tck.entities.Deputy
import spray.json._

trait ChangeContactDeputyTest extends BaseTckTest with DomofonMarshalling with SprayJsonSupport {

  private[this] def deputyUrl(contactId: UUID): String = {
    s"/contacts/${contactId}/deputy"
  }

  private[this] def contactDeputy() = Deputy(
    "Jan Kowalski", "jan.kowalski@company.pl", "+48123321123"
  )

  private[this] def putContactDeputy(contactId: UUID, deputy: Deputy = contactDeputy()) = {
    Put(deputyUrl(contactId), deputy.toJson) ~> domofonRoute
  }

  describe("GET /contacts/{id}/deputy") {
    it("By default Contact has no deputy") {
      val uuid = postContactRequest()

      Get(deputyUrl(uuid)) ~> acceptJson ~> domofonRoute ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }
  }

  describe("PUT /contacts/{id}/deputy") {

    it("It is possible to create deputy with PUT /contacts/{id}/deputy") {
      val uuid = postContactRequest()

      putContactDeputy(uuid) ~> check {
        status shouldBe StatusCodes.OK
      }
    }

    it("Can respond only with application/json and fails with other response types on GET /contacts/{id}/deputy when exists") {
      val uuid = postContactRequest()

      putContactDeputy(uuid) ~> check {
        status shouldBe StatusCodes.OK
      }

      Get(deputyUrl(uuid)) ~> acceptPlain ~> domofonRoute ~> check {
        status shouldBe StatusCodes.NotAcceptable
      }
    }

    it("When deputy was created it could be retrieved GET /contacts/{id}/deputy") {
      val uuid = postContactRequest()
      val deputy = contactDeputy()

      putContactDeputy(uuid, deputy) ~> check {
        status shouldBe StatusCodes.OK
      }

      Get(deputyUrl(uuid)) ~> acceptJson ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Deputy] shouldBe deputy
      }
    }

    it("Deputy could be overwritten") {
      val uuid = postContactRequest()
      val deputy = contactDeputy()
      val deputy2 = deputy.copy(name = "Other Person")

      putContactDeputy(uuid, deputy) ~> check {
        status shouldBe StatusCodes.OK
      }

      Get(deputyUrl(uuid)) ~> acceptJson ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Deputy] shouldBe deputy
      }

      putContactDeputy(uuid, deputy2) ~> check {
        status shouldBe StatusCodes.OK
      }

      Get(deputyUrl(uuid)) ~> acceptJson ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Deputy] shouldBe deputy2
      }
    }
  }

  describe("DELETE /contacts/{id}/deputy") {

    it("Allows deleting even if there is no deputy set ") {
      val uuid = postContactRequest()
      Delete(deputyUrl(uuid)) ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
      }
    }

    it("When deputy was added, it could be removed") {
      val uuid = postContactRequest()
      val deputy = contactDeputy()

      putContactDeputy(uuid, deputy) ~> check {
        status shouldBe StatusCodes.OK
      }

      Get(deputyUrl(uuid)) ~> acceptJson ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Deputy] shouldBe deputy
      }

      Delete(deputyUrl(uuid)) ~> domofonRoute ~> check {
        status shouldBe StatusCodes.OK
      }

      Get(deputyUrl(uuid)) ~> acceptJson ~> domofonRoute ~> check {
        status shouldBe StatusCodes.NotFound
      }

    }

  }

}
