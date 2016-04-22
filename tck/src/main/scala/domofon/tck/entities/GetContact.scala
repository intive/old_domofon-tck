package domofon.tck.entities

case class GetContact(
  id: EntityID,
  name: String,
  notifyEmail: String,
  adminEmail: String,
  isImportant: Boolean,
  fromDate: java.time.LocalDate,
  tillDate: Option[java.time.LocalDate] = None,
  company: Option[String],
  phone: Option[String],
  deputy: Option[Deputy] = None
)

