package domofon.mock.akka.routes

import java.time.LocalDateTime
import java.util.UUID

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.Credentials
import akka.stream.scaladsl.Source
import akka.stream.{Materializer, OverflowStrategy}
import cats.data.Validated.{Invalid, Valid}
import de.heikoseeberger.akkasse.EventStreamMarshalling._
import de.heikoseeberger.akkasse.ServerSentEvent
import domofon.mock.akka.entities._
import domofon.mock.akka.utils.Helpers._
import domofon.mock.akka.utils.RejectionsSupport.{CategoryDoesNotExistRejection, TooManyRequestsRejection}
import domofon.mock.akka.utils._
import spray.json._

import scala.concurrent.duration.{FiniteDuration, _}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait ContactsRoute extends MockMarshallers with SprayJsonSupport {
  self: Auth =>

  implicit def system: ActorSystem

  implicit def materializer: Materializer

  private[this] implicit def executionContext: ExecutionContext = system.dispatcher

  def notifyDelay: FiniteDuration

  def sendNotifications(contactResponse: ContactResponse): Future[NotificationResult]

  def contactsRoute(
    contacts: scala.collection.concurrent.TrieMap[EntityID, ContactResponse],
    categories: scala.collection.concurrent.TrieMap[EntityID, CategoryResponse]
  ): Route = {

    def takeContactFromPath: Directive1[ContactResponse] = {
      pathPrefix(Segment).flatMap { id =>
        contacts.get(id) match {
          case None       => complete((StatusCodes.NotFound, "Contact was not found"))
          case Some(resp) => provide(resp)
        }
      }
    }

    path("contacts") {
      get {
        parameter("category".as[EntityID].?) {
          case None =>
            complete(contacts.values.map(_.toJson(contactPublicResponseWriter)))
          case Some(categoryId) =>
            if (categories.contains(categoryId)) {
              complete(contacts.values.filter(_.category == categoryId).map(_.toJson(contactPublicResponseWriter)))
            } else {
              reject(CategoryDoesNotExistRejection(categoryId))
            }
        }
      } ~
        post {
          entity(as[JsObject]) { json =>
            jsonAs[ContactRequest](json) { cr =>
              ContactRequestValidator(categories.keySet)(cr) match {
                case Valid(contact) =>
                  val id = EntityID.forContact
                  val secret = UUID.randomUUID().toString
                  contacts.update(id, ContactResponse.from(id, secret, cr))
                  broadcastContactsUpdated()
                  complete(ContactCreated(id, secret))
                case Invalid(nel) =>
                  complete((StatusCodes.UnprocessableEntity, ValidationError.fromNel(nel).toJson))
              }

            }
          }
        }
    } ~
      path("contacts" / "sse") {
        complete(sseUpdatesFlow)
      } ~ pathPrefix("contacts") {
        takeContactFromPath { contact =>
          path("deputy") {
            get {
              contact.deputy match {
                case None         => complete((StatusCodes.NotFound, "Contact has no deputy"))
                case Some(deputy) => complete(deputy.toJson)
              }
            } ~
              put {
                authenticateContactSecretOrAdmin(contact) {
                  entity(as[Deputy]) {
                    deputy =>
                      contacts.update(contact.id, contact.copy(deputy = Some(deputy)))
                      broadcastContactsUpdated()
                      complete(OperationSuccessful)
                  }
                }
              } ~
              delete {
                authenticateContactSecretOrAdmin(contact) {
                  contacts.update(contact.id, contact.copy(deputy = None))
                  broadcastContactsUpdated()
                  complete(OperationSuccessful)
                }
              }
          } ~
            path("important") {
              get {
                complete(IsImportant(contact.isImportant))
              } ~
                put {
                  entity(as[JsObject]) { json =>
                    jsonAs[IsImportant](json) { imp =>
                      contacts.update(contact.id, contact.copy(isImportant = imp.isImportant))
                      broadcastContactsUpdated()
                      complete(OperationSuccessful)
                    }
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
                  broadcastContactsUpdated()
                  onComplete(sendNotifications(updatedContact)) {
                    case Success(result) => complete(OperationSuccessful)
                    case Failure(f)      => throw f
                  }
                } else {
                  reject(TooManyRequestsRejection("Can't send notifications that often", contact.nextNotificationAllowedAt))
                }
              }
            } ~
            pathEndOrSingleSlash {
              get {
                complete(contact.toJson(contactPublicResponseWriter))
              } ~
                delete {
                  authenticateContactSecretOrAdmin(contact) {
                    contacts.remove(contact.id)
                    broadcastContactsUpdated()
                    complete(OperationSuccessful)
                  }
                }
            }
        }

      }
  }

  private[this] lazy val publisher = system.actorOf(Props[PublisherActor])
  private[this] lazy val sseUpdatesFlow = Source.actorRef[PublisherActor.Updated.type](1, OverflowStrategy.dropNew)
    .map(_ => ServerSentEvent(SseUpdated(LocalDateTime.now()).toJson.compactPrint, "updated"))
    .keepAlive(1.second, () => ServerSentEvent.heartbeat)
    .mapMaterializedValue(ref => publisher.tell(PublisherActor.Subscribe, ref))

  private[this] def broadcastContactsUpdated() = {
    publisher ! PublisherActor.Updated
  }

}
