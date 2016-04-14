package domofon.mock.akka

import java.time.LocalDateTime

case class TooManyRequestsError(message: String, whenAllowed: Option[LocalDateTime])
