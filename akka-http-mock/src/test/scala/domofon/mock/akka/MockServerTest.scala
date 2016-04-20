package domofon.mock.akka

import domofon.tck.DomofonTck

class MockServerTest extends DomofonTck with MockServer {
  override def serverHostnameAndPort: String = "localhost:12345"
}
