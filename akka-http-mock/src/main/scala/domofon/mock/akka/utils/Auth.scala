package domofon.mock.akka.utils

import java.util.UUID

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.Credentials
import domofon.mock.akka.entities.{EntityID, Secret, LoginToken, ContactResponse}

trait Auth extends AdminCredentials {

  import domofon.mock.akka.utils.MockMarshallers._

  @volatile private[this] var adminSession: Secret = UUID.randomUUID().toString

  def contactSecretAuthenticator(contactResponse: ContactResponse): Authenticator[EntityID] = {
    case p: Credentials.Provided =>
      val isSecret = p.verify(contactResponse.secret.toString)
      val isAdmin = p.verify(adminSession)
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

  def authenticateAdminUserPass: AuthenticatorPF[Secret] = {
    case p @ Credentials.Provided(login) if p.verify(adminPass) && adminLogin == login =>
      adminSession = UUID.randomUUID().toString
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

trait AdminCredentials {
  def adminLogin: String
  def adminPass: String
}

trait AdminCredentialsFromEnv extends AdminCredentials {
  import AdminCredentialsFromEnv._
  def adminLogin: String = sys.env.getOrElse(MockAdminLoginEnv, "admin")
  def adminPass: String = sys.env.getOrElse(MockAdminPasswordEnv, "P4ssw0rd")
}

object AdminCredentialsFromEnv extends AdminCredentialsFromEnv {
  val MockAdminLoginEnv = "MOCK_ADMIN_LOGIN"
  val MockAdminPasswordEnv = "MOCK_ADMIN_PASSWORD"

}
