package domofon.mock

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.{HttpResponse, StatusCodes, MediaTypes}
import akka.http.scaladsl.server._
import domofon.tck.Marshalling
import domofon.tck.entities.{ContactResponse, ContactRequest}

import spray.json._

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

trait MockServer extends Directives with SprayJsonSupport with Marshalling {

  implicit def system: ActorSystem

  def domofonRoute: Route = Route.seal(routes)

  private[this] val contacts = mutable.Map[UUID, ContactResponse]()

  case class MissingRequiredFieldsRejection(message: String, cause: Option[Throwable]) extends Rejection

  private[this] val rejectionHandler: RejectionHandler = RejectionHandler.newBuilder().handle {
    case MissingRequiredFieldsRejection(message, cause) =>
      complete(
        HttpResponse(StatusCodes.UnprocessableEntity, entity = message + cause.map(c => ": " + c.getMessage).getOrElse(""))
      )
  }.result()

  private[this] val routes: Route = handleRejections(rejectionHandler) {
    path("contacts") {
      get {
        complete(contacts.values.toJson)
      } ~
        post {
          entity(as[JsValue]) {
            json =>
              Try(json.convertTo[ContactRequest]) match {
                case Success(contact) =>
                  val id = UUID.randomUUID()
                  contacts.update(id, ContactResponse.from(id, contact))
                  complete(id)
                case Failure(ex) =>
                  reject(MissingRequiredFieldsRejection("Contact requests has wrong structure", Some(ex)))
              }
          }

        }
    } ~ pathPrefix("contacts" / Segment) {
      uuidMaybe =>
        validate(Try(UUID.fromString(uuidMaybe)).isSuccess, "ID must be valid UUID identifier, eg. " + UUID.randomUUID()) {
          get {
            val uuid = UUID.fromString(uuidMaybe)
            contacts.get(uuid) match {
              case None       => complete((StatusCodes.NotFound, "Contact was not found"))
              case Some(resp) => complete(resp.toJson)
            }
          }
        }
    }

  }
}
