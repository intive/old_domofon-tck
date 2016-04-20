package domofon.mock.akka.utils

import java.util.UUID

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.Credentials
import domofon.mock.akka.entities.{LoginToken, ContactResponse}

trait Auth {
  import domofon.mock.akka.utils.MockMarshallers._

  def contactSecretAuthenticator(contactResponse: ContactResponse): Authenticator[UUID] = {
    case p: Credentials.Provided =>
      val isSecret = p.verify(contactResponse.secret.toString)
      val isAdmin = p.verify(adminSession.toString)
      if (isSecret || isAdmin) Some(contactResponse.id) else None
    case _ => None
  }

  def authenticateContactSecretOrAdmin(contactResponse: ContactResponse)(r: Route): Route = {
    authenticateOAuth2("Domofon", contactSecretAuthenticator(contactResponse)) { _ => r }
  }

  val (adminLogin, adminPass) = ("admin", "Z1ON0101") // todo read from config/env
  @volatile private[this] var adminSession = UUID.randomUUID()

  def authenticateAdminUserPass: AuthenticatorPF[UUID] = {
    case p @ Credentials.Provided(login) if p.verify(adminPass) && adminLogin == login =>
      adminSession = UUID.randomUUID()
      adminSession
  }

  lazy val adminSessionRoutes = path("login") {
    get {
      authenticateBasicPF("Domofon Admin", authenticateAdminUserPass) { adminToken =>
        complete(LoginToken(adminToken))
      }
    }
  }
}
