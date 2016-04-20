package domofon.mock.akka

import java.time.LocalDateTime

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Rejection, RejectionHandler}
import domofon.mock.akka.MockMarshallers._

object MockRejections {

  case class MissingRequiredFieldsRejection(message: String, fields: List[String]) extends Rejection
  case class TooManyRequestsRejection(message: String, nextTryAt: Option[LocalDateTime]) extends Rejection
  case object CategoryIsNotBatchRejection extends Rejection

  val rejectionHandler: RejectionHandler = RejectionHandler.newBuilder().handle {
    case MissingRequiredFieldsRejection(message, fields) =>
      complete(
        (StatusCodes.UnprocessableEntity, MissingFieldsError(message, fields))
      )
    case TooManyRequestsRejection(msg, when) =>
      complete(
        (StatusCodes.TooManyRequests, TooManyRequestsError(msg, when))
      )
    case CategoryIsNotBatchRejection =>
      complete(
        StatusCodes.BadRequest
      )
  }.result()

}
