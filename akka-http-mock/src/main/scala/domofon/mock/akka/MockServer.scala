package domofon.mock.akka

import java.time.LocalDateTime
import java.util.UUID

import akka.actor._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server._
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.Source
import de.heikoseeberger.akkasse.{EventStreamMarshalling, ServerSentEvent}
import spray.json._

import scala.collection.mutable
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

trait MockServer extends Directives with SprayJsonSupport with MockMarshallers with EventStreamMarshalling {

  implicit def system: ActorSystem

  private[this] lazy val publisher = system.actorOf(Props[PublisherActor])
  private[this] lazy val sseUpdatesFlow = Source.actorRef[PublisherActor.Updated.type](1, OverflowStrategy.dropNew)
    .map(_ => ServerSentEvent(SseUpdated(LocalDateTime.now()).toJson.compactPrint))
    .keepAlive(1.second, () => ServerSentEvent.heartbeat)
    .mapMaterializedValue(ref => publisher.tell(PublisherActor.Subscribe, ref))

  private[this] def broadcastContactsUpdated() = {
    publisher ! PublisherActor.Updated
  }

  def domofonRoute: Route = Route.seal(routes)

  private[this] lazy val contacts = mutable.Map[UUID, ContactResponse]()

  private[this] case class MissingRequiredFieldsRejection(message: String, cause: Option[Throwable]) extends Rejection

  private[this] lazy val rejectionHandler: RejectionHandler = RejectionHandler.newBuilder().handle {
    case MissingRequiredFieldsRejection(message, cause) =>
      complete(
        HttpResponse(StatusCodes.UnprocessableEntity, entity = message + cause.map(c => ": " + c.getMessage).getOrElse(""))
      )
  }.result()

  private[this] lazy val routes: Route = handleRejections(rejectionHandler) {
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
                  broadcastContactsUpdated
                  complete(id)
                case Failure(ex) =>
                  reject(MissingRequiredFieldsRejection("Contact requests has wrong structure", Some(ex)))
              }
          }

        }
    } ~
      path("contacts" / "sse") {
        complete(sseUpdatesFlow)
      } ~ pathPrefix("contacts" / Segment) {
        uuidMaybe =>
          //FIXME: wrap UUID validation and contact retrieval in helper directive
          validate(Try(UUID.fromString(uuidMaybe)).isSuccess, "ID must be valid UUID identifier, eg. " + UUID.randomUUID()) {
            path("deputy") {
              get {
                val uuid = UUID.fromString(uuidMaybe)
                contacts.get(uuid) match {
                  case None => complete((StatusCodes.NotFound, "Contact was not found"))
                  case Some(resp) => resp.deputy match {
                    case None         => complete((StatusCodes.NotFound, "Contact has no deputy"))
                    case Some(deputy) => complete(deputy.toJson)
                  }
                }
              } ~
                put {
                  entity(as[Deputy]) {
                    deputy =>
                      val uuid = UUID.fromString(uuidMaybe)
                      contacts.get(uuid) match {
                        case None => complete((StatusCodes.NotFound, "Contact was not found"))
                        case Some(resp) =>
                          contacts.update(uuid, resp.copy(deputy = Some(deputy)))
                          broadcastContactsUpdated
                          complete(StatusCodes.OK)
                      }
                  }
                } ~
                delete {
                  val uuid = UUID.fromString(uuidMaybe)
                  contacts.get(uuid) match {
                    case None => complete((StatusCodes.NotFound, "Contact was not found"))
                    case Some(resp) =>
                      contacts.update(uuid, resp.copy(deputy = None))
                      broadcastContactsUpdated
                      complete(StatusCodes.OK)
                  }
                }
            } ~
              get {
                val uuid = UUID.fromString(uuidMaybe)
                contacts.get(uuid) match {
                  case None       => complete((StatusCodes.NotFound, "Contact was not found"))
                  case Some(resp) => complete(resp.toJson)
                }
              }
          }
      } ~ pathEndOrSingleSlash {
        get {
          complete("Mock Server is running, check documentation available at: http://blstream.github.io/domofon-api/")
        }
      }

  }

}
