package domofon.tck.entities

case class ValidationFieldsError(
  message: String,
  fields: List[String]
)
