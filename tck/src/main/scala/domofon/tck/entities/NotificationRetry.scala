package domofon.tck.entities

import java.time.LocalDateTime

case class NotificationRetry(message: String, whenAllowed: LocalDateTime)
