package domofon.tck

import java.util.UUID

import spray.json.{JsArray, JsObject}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import domofon.tck.DomofonMarshalling._
import domofon.tck.entities.CategoryMessage

trait CategoryMessagesTest extends BaseTckTest {
  describe("GET /categories/{id}/messages") {
    it("should respond with list of messages with ids as JSON") {
      val categoryId = postCategoryRequest().id
      Get(s"/categories/$categoryId/messages") ~~> {
        status should equal(StatusCodes.OK)
        entityAs[List[CategoryMessage]] shouldNot be('empty)
      }
    }
  }

  describe("POST /categories/{id}/messages") {
    it("should add new message to category") {
      val categoryId = postCategoryRequest().id
      val msgContent = "This is a funny message :--DDD"
      Post(s"/categories/$categoryId/messages", msgContent) ~> authorizeWithSecret(loginAdmin) ~~> {
        status should equal(StatusCodes.OK)
      }
      Get(s"/categories/$categoryId/messages") ~~> {
        status should equal(StatusCodes.OK)
        entityAs[List[CategoryMessage]].find(_.message == msgContent) shouldNot be('empty)
      }
    }

    it("should respond with UnprocessableEntity for empty message") {
      val categoryId = postCategoryRequest().id
      Post(s"/categories/$categoryId/messages", "") ~> authorizeWithSecret(loginAdmin) ~~> {
        status should equal(StatusCodes.UnprocessableEntity)
      }
    }

    it("should respond with Unauthorized without admin token") {
      val categoryId = postCategoryRequest().id
      Post(s"/categories/$categoryId/messages", "It's a prank bro!") ~~> {
        status should equal(StatusCodes.Unauthorized)
      }
    }
  }

  describe("DELETE /categories/{id}/messages/{messageId}") {
    it("should delete message with given id") {
      val categoryId = postCategoryRequest().id
      val msgContent = "This is a funny message :--DDD"

      var msgId = nonExistentUuid
      Post(s"/categories/$categoryId/messages", msgContent) ~> authorizeWithSecret(loginAdmin) ~~> {
        status should equal(StatusCodes.OK)
        msgId = responseAs[UUID]
      }

      Delete(s"/categories/$categoryId/messages/$msgId") ~> authorizeWithSecret(loginAdmin) ~~> {
        status should equal(StatusCodes.OK)
      }

      Get(s"/categories/$categoryId/messages") ~~> {
        responseAs[List[CategoryMessage]].find(_.id == msgId) should be('empty)
      }
    }

    it("should respond with BadRequest if trying to remove the only message for category") {
      val categoryId = postCategoryRequest().id
      var msgId = nonExistentUuid
      Get(s"/categories/$categoryId/messages") ~~> {
        msgId = responseAs[List[CategoryMessage]].head.id
      }

      Delete(s"/categories/$categoryId/messages/$msgId") ~> authorizeWithSecret(loginAdmin) ~~> {
        status should equal(StatusCodes.BadRequest)
      }
    }

    it("should respond with 404 for non-existent message id") {
      val categoryId = postCategoryRequest().id
      val msgId = nonExistentUuid
      Delete(s"/categories/$categoryId/messages/$msgId") ~> authorizeWithSecret(loginAdmin) ~~> {
        status should equal(StatusCodes.NotFound)
      }
    }

    it("should respond with Unauthorized without admin token") {
      val categoryId = postCategoryRequest().id
      var msgId = nonExistentUuid
      Get(s"/categories/$categoryId/messages") ~~> {
        msgId = responseAs[List[CategoryMessage]].head.id
      }

      Delete(s"/categories/$categoryId/messages/$msgId") ~~> {
        status should equal(StatusCodes.Unauthorized)
      }
    }
  }

  describe("PUT /categories/{id}/messages/{messageId}") {
    it("should allow updating existing message") {
      val categoryId = postCategoryRequest().id
      var msgId = nonExistentUuid
      Get(s"/categories/$categoryId/messages") ~~> {
        msgId = responseAs[List[CategoryMessage]].head.id
      }

      val updatedMessage = "hello world"
      Put(s"/categories/$categoryId/messages/$msgId", updatedMessage) ~> authorizeWithSecret(loginAdmin) ~~> {
        status should equal(StatusCodes.OK)
      }

      Get(s"/categories/$categoryId/messages") ~~> {
        responseAs[List[CategoryMessage]].head.message should equal(updatedMessage)
      }
    }

    it("should respond with Unauthorized without admin token") {
      val categoryId = postCategoryRequest().id
      var msgId = nonExistentUuid
      Get(s"/categories/$categoryId/messages") ~~> {
        msgId = responseAs[List[CategoryMessage]].head.id
      }

      val updatedMessage = "no elo"
      Put(s"/categories/$categoryId/messages/$msgId", updatedMessage) ~~> {
        status should equal(StatusCodes.Unauthorized)
      }
    }
  }
}
