package domofon.tck.entities

case class ContactRequest(
  name: String,
  company: String,
  notifyEmail: String,
  phone: String,
  adminEmail: Option[String] = None,
  fromDate: Option[java.time.LocalDate] = None,
  tillDate: Option[java.time.LocalDate] = None
)
