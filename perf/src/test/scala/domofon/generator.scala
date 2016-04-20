package domofon

import domofon.mock.akka.entities.{Deputy, IsImportant, ContactRequest}
import org.scalacheck._

trait Generators {

  import mock.akka._

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
    adminEmail = _adminEmail,
    fromDate = None,
    tillDate = None
  ))

  def generateDeputy: Deputy = sample(for {
    _name <- name
    _email <- email
    _phone <- phone
    _company <- Gen.some(company)
  } yield Deputy(
    name = _name,
    notifyEmail = _email,
    phone = _phone,
    company = _company
  ))

  def generateImportance: IsImportant = sample(for {
    _importance <- arbitraryBoolean
  } yield IsImportant(_importance))

  private def name = arbitraryString(10, 20)
  private def email = arbitraryString(7, 15) map (e => s"$e@example.com")
  private def company = arbitraryCompany
  private def phone = arbitraryPhone

  private def sample[T](t: Gen[T]): T = t.sample match {
    case Some(x) => x
    case None    => sample(t)
  }

  private def arbitraryBoolean = Gen.oneOf(true, false)

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
