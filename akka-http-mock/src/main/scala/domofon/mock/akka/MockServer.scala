package domofon.mock.akka

import java.util.UUID

import akka.actor._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server._
import akka.http.scaladsl.util.FastFuture
import akka.stream.Materializer
import de.heikoseeberger.akkasse.EventStreamMarshalling
import domofon.mock.akka.entities.{CategoryResponse, ContactResponse, NotificationResult}
import domofon.mock.akka.routes.{CategoriesRoute, ContactsRoute, SwaggerRoute}
import domofon.mock.akka.utils._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

trait MockServer extends Directives
    with SprayJsonSupport
    with MockMarshallers
    with EventStreamMarshalling
    with SwaggerRoute
    with CategoriesRoute
    with ContactsRoute
    with Auth {

  implicit def system: ActorSystem

  implicit def materializer: Materializer

  private[this] implicit def executionContext: ExecutionContext = system.dispatcher

  def domofonRoute: Route = Route.seal(routes)

  private[this] lazy val contacts = collection.concurrent.TrieMap[UUID, ContactResponse]()
  private[this] lazy val categories = collection.concurrent.TrieMap[UUID, CategoryResponse]()

  def notifyDelay: FiniteDuration = 1.minute

  def sendNotifications(contactResponse: ContactResponse): Future[NotificationResult] = {
    println(s"Sending notification to ${contactResponse}")
    FastFuture.successful(NotificationResult("Notification sent", true))
  }

  def sendNotifications(categoryResponse: CategoryResponse): Future[NotificationResult] = {
    println(s"Sending notification to ${categoryResponse}")
    FastFuture.successful(NotificationResult("Notification sent", true))
  }

  private[this] lazy val routes: Route = handleRejections(RejectionsSupport.rejectionHandler) {
    extractRequest { req =>
      //println(req) //for easier debugging of problems you can uncomment that
      domofonYmlRoute ~
        categoriesRoute(categories) ~ contactsRoute(contacts, categories) ~ adminSessionRoutes ~
        pathEndOrSingleSlash {
          get {
            complete("Mock Server is running, check documentation available at: http://blstream.github.io/domofon-api/")
          }
        }
    }
  }

}

object MockServer {

  def apply[T <: AdminCredentials](serverAddress: String, actorSystem: ActorSystem, mat: Materializer, auth: T): MockServer = {
    new MockServer {
      override def adminLogin = auth.adminLogin

      override def adminPass = auth.adminPass

      override implicit def system: ActorSystem = actorSystem

      override implicit def materializer: Materializer = mat

      override def serverHostnameAndPort: String = serverAddress
    }
  }

}