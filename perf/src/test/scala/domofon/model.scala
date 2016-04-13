package com.blstream
package domofon

object model {

  type Name = String
  type Company = String
  type Email = String
  type Phone = String
  type Date = String

  case class ContactRequest(
    name: Name,
    company: Option[Company] = None,
    notifyEmail: Email,
    phone: Phone,
    adminEmail: Option[Email] = None,
    fromDate: Option[Date] = None,
    tillDate: Option[Date] = None
  )

}
