package domofon.mock.akka

case class MissingFieldsError(
  message: String,
  fields: List[String]
)