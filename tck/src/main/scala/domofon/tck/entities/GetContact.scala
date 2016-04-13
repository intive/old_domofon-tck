package domofon.tck.entities

import java.time.LocalDate
import java.util.UUID

case class GetContact(
  id: UUID,
  name: String,
  company: String,
  notifyEmail: String,
  phone: String,
  adminEmail: String,
  isImportant: Boolean,
  message: String,
  fromDate: java.time.LocalDate,
  tillDate: Option[java.time.LocalDate] = None,
  deputy: Option[Deputy] = None
)

case object GetContact {

  def from(id: UUID, req: PostContact): GetContact = {
    GetContact(
      id,
      req.name,
      req.company,
      req.notifyEmail,
      req.phone,
      req.adminEmail.getOrElse(req.notifyEmail),
      false,
      "",
      req.fromDate.getOrElse(LocalDate.now()),
      req.tillDate,
      None
    )

  }

}
