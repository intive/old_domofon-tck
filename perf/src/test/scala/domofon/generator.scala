package com.blstream
package domofon

import org.scalacheck._

trait Generators {

  import model._

  def generateContact: ContactRequest = sample(for {
    _name <- name
    _notifyEmail <- email
    _phone <- phone
    _company <- Gen.some(company)
    _adminEmail <- Gen.some(email)
  } yield ContactRequest(
    name = _name,
    notifyEmail = _notifyEmail,
    phone = _phone,
    company = _company,
    adminEmail = _adminEmail
  ))

  private def name: Gen[Name] = arbitraryString(10, 20) map (new Name(_))
  private def email: Gen[Email] = arbitraryString(7, 15) map (e => s"$e@example.com") map (e => new Email(e))
  private def company: Gen[Company] = arbitraryCompany map (new Company(_))
  private def phone: Gen[Phone] = arbitraryPhone

  private def sample[T](t: Gen[T]): T = t.sample match {
    case Some(x) => x
    case None    => sample(t)
  }

  private def arbitraryString(min: Int, max: Int) = for {
    l <- Gen.choose(min, max)
    em <- Gen.listOfN(l, Gen.alphaChar)
  } yield em.mkString

  private def arbitraryCompany = Gen.oneOf("DHL", "InPost", "UPS", "Poczta")

  private def arbitraryPhone = for {
    x <- Gen.choose(6, 9)
    y <- Gen.listOfN(8, Gen.choose(1, 9))
  } yield s"+48$x${y.mkString}"

}
