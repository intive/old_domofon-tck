package domofon.tck.entities

case class EntityCreated(id: EntityID)

case class EntityCreatedWithSecret(id: EntityID, secret: Token)
