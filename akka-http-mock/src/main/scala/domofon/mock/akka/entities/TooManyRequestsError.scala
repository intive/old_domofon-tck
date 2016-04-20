package domofon.mock.akka.entities

import java.time.LocalDateTime

case class TooManyRequestsError(message: String, whenAllowed: Option[LocalDateTime])
