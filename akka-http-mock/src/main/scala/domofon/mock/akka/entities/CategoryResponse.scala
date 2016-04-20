package domofon.mock.akka.entities

import java.util.UUID

case class CategoryResponse(
  id: UUID,
  name: String,
  description: String,
  messages: List[String],
  isBatch: Boolean,
  nextNotificationAllowedAt: Option[java.time.LocalDateTime] = None
)

object CategoryResponse {
  def from(id: UUID, cr: CategoryRequest): CategoryResponse = {
    CategoryResponse(
      id,
      cr.name,
      cr.description,
      List(cr.message),
      cr.isBatch
    )
  }
}

