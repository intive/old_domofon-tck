package domofon.mock.akka

import domofon.mock.akka.utils.AdminCredentialsFromEnv
import domofon.tck.{DomofonTck, TckEnvCredentials}

class MockServerTest extends DomofonTck with MockServer with AdminCredentialsFromEnv {
  override def serverHostnameAndPort: String = "localhost:12345"

  // using AdminCredentialsFromEnv for TCK credentials
  override def tckAdminLogin: String = adminLogin
  override def tckAdminPass: String = adminPass
}
