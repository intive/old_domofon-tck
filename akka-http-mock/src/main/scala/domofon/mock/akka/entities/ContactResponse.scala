package domofon.mock.akka.entities

import java.time.LocalDate

case class ContactResponse(
  id: EntityID,
  secret: EntityID,
  name: String,
  category: EntityID,
  company: Option[String],
  notifyEmail: String,
  phone: Option[String],
  adminEmail: String,
  isImportant: Boolean,
  fromDate: java.time.LocalDate,
  tillDate: Option[java.time.LocalDate],
  deputy: Option[Deputy],
  nextNotificationAllowedAt: Option[java.time.LocalDateTime] = None
)

case object ContactResponse {

  def from(id: EntityID, secret: Secret, req: ContactRequest): ContactResponse = {
    ContactResponse(
      id,
      secret,
      req.name,
      req.category,
      req.company,
      req.notifyEmail,
      req.phone,
      req.adminEmail.getOrElse(req.notifyEmail),
      false,
      req.fromDate.getOrElse(LocalDate.now()),
      req.tillDate,
      None
    )

  }

}