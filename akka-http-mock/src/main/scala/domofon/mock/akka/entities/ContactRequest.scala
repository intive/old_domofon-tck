package domofon.mock.akka.entities

case class ContactRequest(
  name: String,
  company: Option[String] = None,
  notifyEmail: String,
  phone: String,
  adminEmail: Option[String] = None,
  fromDate: Option[java.time.LocalDate] = None,
  tillDate: Option[java.time.LocalDate] = None
)

case object ContactRequest {
  def requiredFields: Set[String] = Set("name", "notifyEmail", "phone")
}
