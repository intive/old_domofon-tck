package domofon.mock.akka

import java.time.{LocalDate, ZonedDateTime}

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.data.Validated._
import cats.syntax.cartesian._
import cats.std.list._
import cats.SemigroupK
import cats.data.Validated.{Invalid, Valid}
import cats.syntax.apply
import uk.gov.hmrc.emailaddress.EmailAddress

import scala.util.{Failure, Success, Try}

object Validators {
  type Error = (FieldName, Message)
  type FieldName = String
  type Message = String

  def nonEmpty: String => ValidatedNel[Message, String] = { s =>
    if(s.nonEmpty) Valid(s)
    else Invalid(NonEmptyList(s"string cannot be empty"))
  }

  def validEmail: String => ValidatedNel[Message, String] = { email =>
    if(EmailAddress.isValid(email))
      Valid(email)
    else
      Invalid(NonEmptyList(s"email $email is invalid"))
  }

  def validDate(date: String): ValidatedNel[Message, ZonedDateTime] = {
    Try(java.time.ZonedDateTime.parse(date)) match {
      case Success(zdt) => Valid(zdt)
      case Failure(_) => Invalid(NonEmptyList(s"date $date is invalid"))
    }
  }

  def validDateRange(fromO: Option[LocalDate], tillO: Option[LocalDate]):
  ValidatedNel[Message, Option[(LocalDate, LocalDate)]] = {
    (fromO, tillO) match {
      case (Some(from), Some(till)) if from.isBefore(till) || from.isEqual(till) =>
        Valid(Some(from, till))
      case (None, None) =>
        Valid(None)
      case (Some(_), Some(_)) =>
        Invalid(NonEmptyList("\"from\" date is after \"till\" date"))
      case _ =>
        Invalid(NonEmptyList("either both dates must be defined or none"))
    }
  }
}

object ContactRequestValidator {
  import Validators._

  implicit val nelSemigroup = SemigroupK[NonEmptyList].algebra[Message]

  def apply(cr: ContactRequest): ValidatedNel[Message, ContactRequest] = {
    (validEmail(cr.notifyEmail) |@|
      nonEmpty(cr.name) |@|
      validDateRange(cr.fromDate, cr.tillDate)
      ).map { (_, _, _) => cr }
  }
}
