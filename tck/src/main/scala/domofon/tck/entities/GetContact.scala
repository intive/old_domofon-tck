package domofon.tck.entities

import java.util.UUID

case class GetContact(
  id: UUID,
  name: String,
  company: String,
  notifyEmail: String,
  phone: String,
  adminEmail: String,
  isImportant: Boolean,
  fromDate: java.time.LocalDate,
  tillDate: Option[java.time.LocalDate] = None,
  deputy: Option[Deputy] = None
)

