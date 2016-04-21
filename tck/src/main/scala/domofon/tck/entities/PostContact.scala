package domofon.tck.entities

import java.util.UUID

case class PostContact(
  name: String,
  category: UUID,
  notifyEmail: String,
  company: Option[String] = None,
  phone: Option[String] = None,
  adminEmail: Option[String] = None,
  fromDate: Option[java.time.LocalDate] = None,
  tillDate: Option[java.time.LocalDate] = None
)
