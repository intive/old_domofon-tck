package domofon.mock.akka

import domofon.mock.akka.utils.AdminCredentialsFromEnv
import domofon.tck.DomofonTck

class MockServerTest extends DomofonTck with MockServer with AdminCredentialsFromEnv {
  override def serverHostnameAndPort: String = "localhost:12345"
}
