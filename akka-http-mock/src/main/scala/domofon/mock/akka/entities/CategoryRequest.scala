package domofon.mock.akka.entities

case class CategoryRequest(
  name: String,
  description: String,
  isBatch: Boolean,
  message: String
)

case object CategoryRequest {
  val requiredFields = Set("name", "description", "isBatch", "message")
}

