package domofon.mock.akka.utils

import java.time.LocalDateTime
import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Rejection, RejectionHandler}
import MockMarshallers._
import domofon.mock.akka.entities.{CategoryDoesNotExistError, CategoryIsIndividualError, MissingFieldsError, TooManyRequestsError}

object RejectionsSupport {

  case class MissingRequiredFieldsRejection(message: String, fields: List[String]) extends Rejection
  case class TooManyRequestsRejection(message: String, nextTryAt: Option[LocalDateTime]) extends Rejection
  case object CategoryIsIndividualRejection extends Rejection
  case class CategoryDoesNotExistRejection(category: UUID) extends Rejection

  val rejectionHandler: RejectionHandler = RejectionHandler.newBuilder().handle {
    case MissingRequiredFieldsRejection(message, fields) =>
      complete(
        (StatusCodes.UnprocessableEntity, MissingFieldsError(message, fields))
      )
    case TooManyRequestsRejection(msg, when) =>
      complete(
        (StatusCodes.TooManyRequests, TooManyRequestsError(msg, when))
      )
    case CategoryIsIndividualRejection =>
      complete(
        (StatusCodes.BadRequest, CategoryIsIndividualError)
      )
    case CategoryDoesNotExistRejection(categoryId) =>
      complete(
        (StatusCodes.BadRequest, CategoryDoesNotExistError(categoryId))
      )
  }.result()

}
