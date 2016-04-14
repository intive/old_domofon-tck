package domofon.tck.entities

import java.time.LocalDate
import java.util.UUID

case class GetContact(
  id: UUID,
  name: String,
  company: String,
  notifyEmail: String,
  phone: String,
  adminEmail: Option[String],
  isImportant: Option[Boolean],
  message: Option[String],
  fromDate: Option[java.time.LocalDate] = Some(LocalDate.now()),
  tillDate: Option[java.time.LocalDate] = None,
  deputy: Option[Deputy] = None
)

