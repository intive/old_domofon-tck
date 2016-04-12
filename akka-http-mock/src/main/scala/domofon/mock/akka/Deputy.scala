package domofon.mock.akka

case class Deputy(
  name: String,
  notifyEmail: String,
  phone: String,
  company: Option[String] = None
)
