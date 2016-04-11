package domofon.tck

import java.time.LocalDate
import java.util.UUID

import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.unmarshalling.{PredefinedFromEntityUnmarshallers, FromEntityUnmarshaller}
import domofon.tck.entities.{ContactResponse, ContactCreateResponse, ContactRequest}
import spray.json._

trait Marshalling extends DefaultJsonProtocol {

  implicit object LocalDateJsonFormat extends RootJsonFormat[LocalDate] {

    override def write(obj: LocalDate) = JsString(obj.toString)

    override def read(json: JsValue): LocalDate = json match {
      case JsString(s) => LocalDate.parse(s)
      case _           => throw new DeserializationException("Date is not a String")
    }
  }

  implicit val contactRequestFormat = jsonFormat7(ContactRequest.apply)
  implicit val contactResponseFormat = jsonFormat7(ContactResponse.apply)

  implicit val contactCreateResponseFormat = jsonFormat1(ContactCreateResponse.apply)

  implicit val contactCreatedMarshaller: ToEntityMarshaller[UUID] = Marshaller.oneOf(
    Marshaller.StringMarshaller.wrap(MediaTypes.`text/plain`)(uuid => uuid.toString),
    Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`)(uuid => s"""{"id":"${uuid}"}""")
  )

  implicit val rawUUIDEntityUnmarshaller: FromEntityUnmarshaller[UUID] =
    PredefinedFromEntityUnmarshallers.stringUnmarshaller.map(UUID.fromString(_))

}

object Marshalling extends Marshalling
