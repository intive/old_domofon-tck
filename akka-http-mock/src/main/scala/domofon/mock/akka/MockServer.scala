package domofon.mock.akka

import java.time.LocalDateTime
import java.util.UUID

import akka.actor._
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.http.scaladsl.client.RequestBuilding._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.util.FastFuture
import akka.stream.{Materializer, OverflowStrategy}
import akka.stream.scaladsl.Source
import cats.data.Validated.{Invalid, Valid}
import de.heikoseeberger.akkasse.{EventStreamMarshalling, ServerSentEvent}
import spray.json._

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

trait MockServer extends Directives with SprayJsonSupport with MockMarshallers with EventStreamMarshalling {

  implicit def system: ActorSystem

  private[this] implicit def executionContext: ExecutionContext = system.dispatcher

  implicit def materializer: Materializer

  private[this] lazy val publisher = system.actorOf(Props[PublisherActor])
  private[this] lazy val sseUpdatesFlow = Source.actorRef[PublisherActor.Updated.type](1, OverflowStrategy.dropNew)
    .map(_ => ServerSentEvent(SseUpdated(LocalDateTime.now()).toJson.compactPrint, "updated"))
    .keepAlive(1.second, () => ServerSentEvent.heartbeat)
    .mapMaterializedValue(ref => publisher.tell(PublisherActor.Subscribe, ref))

  private[this] def broadcastContactsUpdated() = {
    publisher ! PublisherActor.Updated
  }

  def domofonRoute: Route = Route.seal(routes)

  private[this] lazy val contacts = collection.concurrent.TrieMap[UUID, ContactResponse]()

  private[this] case class MissingRequiredFieldsRejection(message: String, fields: List[String]) extends Rejection

  private[this] case class TooManyRequestsRejection(message: String, nextTryAt: Option[LocalDateTime]) extends Rejection

  private[this] lazy val rejectionHandler: RejectionHandler = RejectionHandler.newBuilder().handle {
    case MissingRequiredFieldsRejection(message, fields) =>
      complete(
        (StatusCodes.UnprocessableEntity, MissingFieldsError(message, fields))
      )
    case TooManyRequestsRejection(msg, when) =>
      complete(
        (StatusCodes.TooManyRequests, TooManyRequestsError(msg, when))
      )
  }.result()

  private[this] def takeContactFromPath: Directive1[ContactResponse] = {
    pathPrefix(Segment).flatMap {
      uuidMaybe =>
        Try(UUID.fromString(uuidMaybe)) match {
          case Success(uuid) =>
            contacts.get(uuid) match {
              case None       => complete((StatusCodes.NotFound, "Contact was not found"))
              case Some(resp) => provide(resp)
            }
          case Failure(e) =>
            reject(ValidationRejection("ID must be valid UUID identifier, eg. " + UUID.randomUUID()))
        }
    }
  }

  def notifyDelay: FiniteDuration = 1.minute

  case class NotificationResult(message: String, wasSuccessfull: Boolean)

  def sendNotifications(contactResponse: ContactResponse): Future[NotificationResult] = {
    println(s"Sending notification to ${contactResponse}")
    FastFuture.successful(NotificationResult("Notification sent", true))
  }

  private[this] lazy val routes: Route = handleRejections(rejectionHandler) {
    extractRequest {
      req =>
        //println(req) //for easier debugging of problems you can uncomment that

        path("domofon.yaml") {
          get {
            onComplete(
              Http().singleRequest(
                Get("https://raw.githubusercontent.com/blstream/domofon-api/gh-pages/domofon.yaml")
              ).flatMap {
                  req => Unmarshal(req).to[String]
                }
            ) {
                case Success(r) => complete(r)
                case Failure(f) => throw f
              }
          }
        } ~ path("contacts") {
          get {
            complete(contacts.values.map(_.toJson(contactWithoutMessageWriter)))
          } ~
            post {
              entity(as[JsObject]) {
                json =>
                  Try(json.convertTo[ContactRequest]) match {
                    case Success(contact) =>
                      ContactRequestValidator(contact) match {
                        case Valid(cr) =>
                          val id = UUID.randomUUID()
                          contacts.update(id, ContactResponse.from(id, contact))
                          broadcastContactsUpdated
                          complete(id)
                        case Invalid(nel) =>
                          complete((StatusCodes.UnprocessableEntity, ValidationError.fromNel(nel).toJson))
                      }

                    case Failure(DeserializationException(msg, ex, fields)) =>
                      reject(MissingRequiredFieldsRejection(msg, fields))
                    case Failure(otherEx) =>
                      reject()
                  }
              }
            }
        } ~
          path("contacts" / "sse") {
            complete(sseUpdatesFlow)
          } ~ pathPrefix("contacts") {
            takeContactFromPath {
              contact =>
                path("deputy") {
                  get {
                    contact.deputy match {
                      case None         => complete((StatusCodes.NotFound, "Contact has no deputy"))
                      case Some(deputy) => complete(deputy.toJson)
                    }
                  } ~
                    put {
                      entity(as[Deputy]) {
                        deputy =>
                          contacts.update(contact.id, contact.copy(deputy = Some(deputy)))
                          broadcastContactsUpdated
                          complete(StatusCodes.OK)
                      }
                    } ~
                    delete {
                      contacts.update(contact.id, contact.copy(deputy = None))
                      broadcastContactsUpdated
                      complete(StatusCodes.OK)
                    }
                } ~
                  path("important") {
                    get {
                      complete(IsImportant(contact.isImportant).toJson)
                    } ~
                      put {
                        entity(as[IsImportant]) {
                          imp =>
                            contacts.update(contact.id, contact.copy(isImportant = imp.isImportant))
                            broadcastContactsUpdated
                            complete(StatusCodes.OK)
                        }
                      }
                  } ~
                  path("notify") {
                    post {
                      if (contact.nextNotificationAllowedAt.map(!_.isAfter(LocalDateTime.now)).getOrElse(true)) {
                        val updatedContact = contact.copy(
                          nextNotificationAllowedAt = Some(LocalDateTime.now.plusSeconds(notifyDelay.toSeconds))
                        )
                        contacts.update(contact.id, updatedContact)
                        broadcastContactsUpdated
                        onComplete(sendNotifications(updatedContact)) {
                          case Success(result) => complete(StatusCodes.OK)
                          case Failure(f)      => throw f
                        }
                      } else {
                        reject(TooManyRequestsRejection("Can't send notifications that often", contact.nextNotificationAllowedAt))
                      }
                    }
                  } ~
                  path("message") {
                    get {
                      complete(contact.message)
                    } ~
                      put {
                        entity(as[String]) { msg =>
                          contacts.update(contact.id, contact.copy(message = msg))
                          complete(ContactMessageUpdated("OK"))
                        }
                      }
                  } ~
                  pathEndOrSingleSlash {
                    get {
                      complete(contact.toJson(contactWithoutMessageWriter))
                    } ~
                      delete {
                        contacts.remove(contact.id)
                        broadcastContactsUpdated()
                        complete(StatusCodes.OK)
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

}

case object MockServer {

  def apply(actorSystem: ActorSystem, mat: Materializer): MockServer = {
    new MockServer {
      override implicit def system: ActorSystem = actorSystem

      override implicit def materializer: Materializer = mat
    }
  }

}