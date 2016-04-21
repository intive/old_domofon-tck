package domofon.mock.akka.entities

case class CategoryRequest(
  name: String,
  description: String,
  message: String,
  isIndividual: Boolean = false
)

case object CategoryRequest {
  val requiredFields = Set("name", "description", "message")
}

