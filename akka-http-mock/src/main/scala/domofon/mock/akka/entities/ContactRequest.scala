package domofon.mock.akka.entities

import java.util.UUID

case class ContactRequest(
  name: String,
  category: UUID,
  notifyEmail: String,
  phone: Option[String] = None,
  company: Option[String] = None,
  adminEmail: Option[String] = None,
  fromDate: Option[java.time.LocalDate] = None,
  tillDate: Option[java.time.LocalDate] = None
)

case object ContactRequest {
  def requiredFields: Set[String] = Set("name", "notifyEmail")
}
