package domofon.mock.akka

import java.time.LocalDate
import java.util.UUID

case class ContactResponse(
  id: UUID,
  secret: UUID,
  name: String,
  company: Option[String],
  notifyEmail: String,
  phone: String,
  adminEmail: String,
  isImportant: Boolean,
  message: String,
  fromDate: java.time.LocalDate,
  tillDate: Option[java.time.LocalDate],
  deputy: Option[Deputy],
  nextNotificationAllowedAt: Option[java.time.LocalDateTime] = None
)

case object ContactResponse {

  def from(id: UUID, secret: UUID, req: ContactRequest): ContactResponse = {
    ContactResponse(
      id,
      secret,
      req.name,
      req.company,
      req.notifyEmail,
      req.phone,
      req.adminEmail.getOrElse(req.notifyEmail),
      false,
      "",
      req.fromDate.getOrElse(LocalDate.now()),
      req.tillDate,
      None
    )

  }

}