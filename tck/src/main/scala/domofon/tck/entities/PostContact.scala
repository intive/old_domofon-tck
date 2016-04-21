package domofon.tck.entities

import java.util.UUID

case class PostContact(
  name: String,
  category: UUID,
  company: String,
  notifyEmail: String,
  phone: String,
  adminEmail: Option[String] = None,
  fromDate: Option[java.time.LocalDate] = None,
  tillDate: Option[java.time.LocalDate] = None
)
