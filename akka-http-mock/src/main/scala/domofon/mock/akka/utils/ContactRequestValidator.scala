package domofon.mock.akka.utils

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.data.Validated._
import cats.syntax.cartesian._
import cats.std.list._
import cats.SemigroupK
import cats.Functor
import cats.data.Validated.{Invalid, Valid}
import cats.syntax.apply
import domofon.mock.akka.entities.{EntityID, ContactRequest}
import uk.gov.hmrc.emailaddress.EmailAddress

import scala.util.{Failure, Success, Try}

object ContactRequestValidator {
  import Validators._

  def apply(categoryIds: scala.collection.Set[EntityID])(cr: ContactRequest): ValidatedNel[Error, ContactRequest] = {
    (field("email")(validEmail)(cr.notifyEmail) |@|
      field("name")(nonEmptyString)(cr.name) |@|
      field("category")(existsElement(categoryIds, "category"))(cr.category) |@|
      field("phone")(optional(nonEmptyString))(cr.phone) |@|
      field("company")(optional(nonEmptyString))(cr.company) |@|
      field("adminEmail")(optional(validEmail))(cr.adminEmail) |@|
      field("fromDate")(validDateRange _ tupled)((cr.fromDate, cr.tillDate))).map { (_, _, _, _, _, _, _) => cr }
  }
}
