package domofon.mock.akka.utils

import java.time.{LocalDate, ZonedDateTime}

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.data.Validated._
import cats.syntax.cartesian._
import cats.std.list._
import cats.SemigroupK
import cats.Functor
import cats.data.Validated.{Invalid, Valid}
import cats.syntax.functor._
import uk.gov.hmrc.emailaddress.EmailAddress

import scala.util.{Failure, Success, Try}

object Validators {
  type Error = (FieldName, Message)
  type FieldName = String
  type Message = String

  implicit val nelErrorSemigroup = SemigroupK[NonEmptyList].algebra[Error]

  def field[A, B](fieldName: FieldName)(validation: A => ValidatedNel[Message, B]): A => ValidatedNel[Error, B] = { value =>
    validation(value).leftMap(_.map(msg => (fieldName, msg)))
  }

  def nonEmptyString: String => ValidatedNel[Message, String] = { s =>
    if (s.nonEmpty) Valid(s)
    else Invalid(NonEmptyList(s"string cannot be empty"))
  }

  def existsElement[T](elements: scala.collection.Set[T], elementName: String = "element"): T => ValidatedNel[Message, T] = { s =>
    if (elements.contains(s)) Valid(s)
    else Invalid(NonEmptyList(s"${elementName} does not exists"))
  }

  def optional[A, B](validation: A => ValidatedNel[Message, B]): Option[A] => ValidatedNel[Message, Option[B]] = {
    case Some(a) => validation(a).map(v => Option(v))
    case None    => Valid(None)
  }

  def validEmail: String => ValidatedNel[Message, String] = { email =>
    if (EmailAddress.isValid(email))
      Valid(email)
    else
      Invalid(NonEmptyList(s"email $email is invalid"))
  }

  def validDateRange(fromO: Option[LocalDate], tillO: Option[LocalDate]): ValidatedNel[Message, Option[(LocalDate, LocalDate)]] = {
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

