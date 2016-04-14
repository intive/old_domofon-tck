package domofon.mock.akka

case class ContactRequest(
  name: String,
  company: Option[String],
  notifyEmail: String,
  phone: String,
  adminEmail: Option[String],
  fromDate: Option[java.time.LocalDate],
  tillDate: Option[java.time.LocalDate]
)

case object ContactRequest {
  def requiredFields: Set[String] = Set("name", "company", "notifyEmail", "phone")
}
