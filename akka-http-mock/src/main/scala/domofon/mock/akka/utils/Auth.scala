package domofon.mock.akka.utils

import java.util.UUID

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.Credentials
import domofon.mock.akka.entities.{LoginToken, ContactResponse}

trait Auth {
  import domofon.mock.akka.utils.MockMarshallers._

  @volatile private[this] var adminSession = UUID.randomUUID()

  lazy val adminLogin = sys.env.getOrElse("MOCK_ADMIN_LOGIN", "admin")
  lazy val adminPass = sys.env.getOrElse("MOCK_ADMIN_PASSWORD", "P4ssw0rd")

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

  def authenticateAdminToken(r: Route): Route = {
    authenticateOAuth2PF("Domofon Admin", {
      case p: Credentials.Provided if p.verify(adminSession.toString) => p.identifier
    }) { _ => r }
  }

  def authenticateAdminUserPass: AuthenticatorPF[UUID] = {
    case p @ Credentials.Provided(login) if p.verify(adminPass) && adminLogin == login =>
      adminSession = UUID.randomUUID()
      adminSession
  }

  def adminSessionRoutes = path("login") {
    get {
      authenticateBasicPF("Domofon Admin", authenticateAdminUserPass) { adminToken =>
        complete(LoginToken(adminToken))
      }
    }
  }
}
