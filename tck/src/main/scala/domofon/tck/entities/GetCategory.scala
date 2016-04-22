package domofon.tck.entities


case class GetCategory(
  id: EntityID,
  name: String,
  description: String,
  messages: Map[EntityID, String],
  isIndividual: Boolean
)

