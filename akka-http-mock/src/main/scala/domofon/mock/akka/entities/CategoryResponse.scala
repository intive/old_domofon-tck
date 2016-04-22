package domofon.mock.akka.entities

import java.util.UUID

import domofon.mock.akka.utils.EntityID

case class CategoryResponse(
  id: EntityID,
  name: String,
  description: String,
  messages: Map[EntityID, String],
  isIndividual: Boolean,
  nextNotificationAllowedAt: Option[java.time.LocalDateTime] = None
)

object CategoryResponse {
  def from(id: EntityID, cr: CategoryRequest): CategoryResponse = {
    CategoryResponse(
      id,
      cr.name,
      cr.description,
      Map(EntityID.forCategory -> cr.message),
      cr.isIndividual
    )
  }
}

