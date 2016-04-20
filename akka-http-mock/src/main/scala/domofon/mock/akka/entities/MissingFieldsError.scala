package domofon.mock.akka.entities

case class MissingFieldsError(
  message: String,
  fields: List[String]
)