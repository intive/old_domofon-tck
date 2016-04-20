package domofon.mock.akka

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import domofon.mock.akka.MockRejections.MissingRequiredFieldsRejection
import spray.json.{DeserializationException, JsObject, JsonReader}

import scala.util.{Failure, Success, Try}

trait Helpers {
  def jsonAs[T: JsonReader](json: JsObject)(f: T => Route): Route = {
    Try(json.convertTo[T]) match {
      case Success(x) =>
        f(x)
      case Failure(DeserializationException(msg, ex, fields)) =>
        reject(MissingRequiredFieldsRejection(msg, fields))
      case Failure(otherEx) =>
        reject()
    }
  }
}

case object Helpers extends Helpers
