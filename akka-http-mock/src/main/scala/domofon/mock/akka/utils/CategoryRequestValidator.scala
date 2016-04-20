package domofon.mock.akka.utils

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.data.Validated._
import cats.syntax.cartesian._
import cats.std.list._
import cats.SemigroupK
import cats.Functor
import cats.data.Validated.{Invalid, Valid}
import cats.syntax.apply
import domofon.mock.akka.entities.{CategoryRequest}

object CategoryRequestValidator {

  import Validators._

  def apply(cr: CategoryRequest): ValidatedNel[Error, CategoryRequest] = {
    (field("name")(nonEmptyString)(cr.name) |@|
      field("description")(nonEmptyString)(cr.description) |@|
      field("message")(nonEmptyString)(cr.message)).map { (_, _, _) => cr }
  }
}
