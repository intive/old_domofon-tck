package domofon.tck.entities

import java.time.LocalDate

case class ContactResponse(
  name: String,
  company: String,
  notifyEmail: String,
  phone: String,
  adminEmail: String,
  fromDate: java.time.LocalDate,
  tillDate: Option[java.time.LocalDate] = None
)

case object ContactResponse {

  def from(req: ContactRequest): ContactResponse = {
    ContactResponse(
      req.name,
      req.company,
      req.notifyEmail,
      req.phone,
      req.adminEmail.getOrElse(req.notifyEmail),
      req.fromDate.getOrElse(LocalDate.now()),
      req.tillDate
    )

  }

}