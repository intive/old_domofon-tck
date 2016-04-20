package domofon.tck.entities

import java.util.UUID

case class EntityCreated(id: UUID)

case class EntityCreatedWithSecret(id: UUID, secret: UUID)
