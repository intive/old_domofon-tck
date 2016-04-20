package domofon

import akka.http.scaladsl.marshallers.sprayjson._
import domofon.mock.akka.utils.MockMarshallers
import spray.json._

trait Feeders extends SprayJsonSupport with MockMarshallers {
  self: Generators =>

  def contactFeeder = Iterator.continually(
    Map(
      "contact" -> generateContact.toJson.prettyPrint,
      "deputy" -> generateDeputy.toJson.prettyPrint,
      "importance" -> generateImportance.toJson.prettyPrint
    )
  )
}
