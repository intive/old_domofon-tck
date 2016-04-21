package domofon.tck.entities

import java.util.UUID

case class GetCategory(
  id: UUID,
  name: String,
  description: String,
  messages: List[String],
  isIndividual: Boolean
)

