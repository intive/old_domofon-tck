package domofon.tck.entities


case class PostContact(
  name: String,
  category: EntityID,
  notifyEmail: String,
  company: Option[String] = None,
  phone: Option[String] = None,
  adminEmail: Option[String] = None,
  fromDate: Option[java.time.LocalDate] = None,
  tillDate: Option[java.time.LocalDate] = None
)
