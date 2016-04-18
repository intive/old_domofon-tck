package domofon.mock.akka

import cats.data.NonEmptyList
import cats.std.all._

case class ValidationError(messages: List[Validators.Message] = List.empty, fields: List[Validators.FieldName] = List.empty){
  def update(err: Validators.Error): ValidationError = {
    copy(messages = err._2 :: messages, fields = err._1 :: fields)
  }
}
object ValidationError {
  def fromNel(nel: NonEmptyList[Validators.Error]): ValidationError = {
    nel.foldLeft(ValidationError()) { (acc, fieldAndMessage) => acc.update(fieldAndMessage) }
  }
}
