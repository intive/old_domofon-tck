package domofon.mock.akka.entities

import java.util.UUID

case class CategoryResponse(
  id: UUID,
  name: String,
  description: String,
  messages: Map[UUID, String],
  isIndividual: Boolean,
  nextNotificationAllowedAt: Option[java.time.LocalDateTime] = None
)

object CategoryResponse {
  def from(id: UUID, cr: CategoryRequest): CategoryResponse = {
    CategoryResponse(
      id,
      cr.name,
      cr.description,
      Map(UUID.randomUUID() -> cr.message),
      cr.isIndividual
    )
  }
}

