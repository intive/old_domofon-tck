package domofon.mock.akka.routes

import java.time.LocalDateTime

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.Materializer
import cats.data.Validated.{Invalid, Valid}
import domofon.mock.akka.entities._
import domofon.mock.akka.utils.Helpers._
import domofon.mock.akka.utils.RejectionsSupport.{CategoryIsIndividualRejection, TooManyRequestsRejection}
import domofon.mock.akka.utils._
import spray.json._

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait CategoriesRoute extends MockMarshallers with SprayJsonSupport with Auth {

  implicit def system: ActorSystem

  implicit def materializer: Materializer

  private[this] implicit def executionContext: ExecutionContext = system.dispatcher

  def notifyDelay: FiniteDuration

  def sendNotifications(categoryResponse: CategoryResponse): Future[NotificationResult]

  def categoriesRoute(categories: scala.collection.concurrent.TrieMap[EntityID, CategoryResponse]): Route = {

    def takeCategoryFromPath: Directive1[CategoryResponse] = {
      pathPrefix(Segment).flatMap {
        uuid =>
          categories.get(uuid) match {
            case None       => complete((StatusCodes.NotFound, "Category was not found"))
            case Some(resp) => provide(resp)
          }
      }
    }

    path("categories") {
      get {
        complete(categories.values.toJson)
      } ~
        post {
          authenticateAdminToken {
            entity(as[JsObject]) { json =>
              jsonAs[CategoryRequest](json) { cr =>
                CategoryRequestValidator(cr) match {
                  case Valid(contact) =>
                    val id = EntityID.forCategory
                    categories.update(id, CategoryResponse.from(id, cr))
                    complete(CategoryCreated(id))
                  case Invalid(nel) =>
                    complete((StatusCodes.UnprocessableEntity, ValidationError.fromNel(nel).toJson))
                }
              }
            }
          }
        }
    } ~
      pathPrefix("categories") {
        takeCategoryFromPath { category =>
          path("notify") {
            post {
              if (category.isIndividual) {
                reject(CategoryIsIndividualRejection)
              } else {
                if (category.nextNotificationAllowedAt.map(!_.isAfter(LocalDateTime.now)).getOrElse(true)) {
                  val updatedCategory = category.copy(
                    nextNotificationAllowedAt = Some(LocalDateTime.now.plusSeconds(notifyDelay.toSeconds))
                  )
                  categories.update(category.id, updatedCategory)
                  onComplete(sendNotifications(updatedCategory)) {
                    case Success(result) => complete(OperationSuccessful)
                    case Failure(f)      => throw f
                  }
                } else {
                  reject(TooManyRequestsRejection("Can't send notifications that often", category.nextNotificationAllowedAt))
                }
              }
            }
          } ~
            pathPrefix("messages") {
              pathEndOrSingleSlash {
                post {
                  authenticateAdminToken {
                    entity(as[String]) { message =>
                      if (message.isEmpty) complete(StatusCodes.UnprocessableEntity)
                      else {
                        val msgId = EntityID.forMessage
                        categories.update(category.id, category.copy(messages = category.messages.updated(msgId, message)))
                        complete(msgId)
                      }
                    }
                  }
                }
              } ~ path(Segment) { msgId =>
                delete {
                  authenticateAdminToken {
                    if (category.messages.contains(msgId) && category.messages.size > 1) {
                      categories.update(category.id, category.copy(messages = category.messages - msgId))
                      complete(OperationSuccessful)
                    } else if (!category.messages.contains(msgId)) {
                      complete(StatusCodes.NotFound)
                    } else complete(StatusCodes.BadRequest)
                  }
                } ~ put {
                  authenticateAdminToken {
                    entity(as[String]) { msg =>
                      if (msg.isEmpty) complete(StatusCodes.UnprocessableEntity)
                      else {
                        categories.update(category.id, category.copy(messages = category.messages.updated(msgId, msg)))
                        complete(OperationSuccessful)
                      }
                    }
                  }
                }
              } ~ get {
                val jsonMsgs = category.messages.map { case (id, msg) => JsObject("id" -> id.toJson, "message" -> JsString(msg)) }
                complete(jsonMsgs.toJson)
              }
            } ~
            pathEndOrSingleSlash {
              get {
                complete(category.toJson)
              } ~
                delete {
                  authenticateAdminToken {
                    categories.remove(category.id)
                    complete(OperationSuccessful)
                  }
                }
            }
        }
      }

  }

}
