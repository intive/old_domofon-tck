package domofon.mock.akka.entities

case class Deputy(
  name: String,
  notifyEmail: String,
  phone: String,
  company: Option[String] = None
)
