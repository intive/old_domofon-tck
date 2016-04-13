package domofon.tck

import java.time.{LocalDate, LocalDateTime}
import java.util.UUID

import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, PredefinedFromEntityUnmarshallers}
import domofon.tck.entities._
import spray.json._

trait DomofonMarshalling extends DefaultJsonProtocol {

  implicit object LocalDateJsonFormat extends RootJsonFormat[LocalDate] {

    override def write(obj: LocalDate) = JsString(obj.toString)

    override def read(json: JsValue): LocalDate = json match {
      case JsString(s) => LocalDate.parse(s)
      case _           => throw new DeserializationException("Expected Date as String in YYYY-mm-dd format")
    }
  }

  implicit object LocalDateTimeJsonFormat extends RootJsonFormat[LocalDateTime] {

    override def write(obj: LocalDateTime) = JsString(obj.toString)

    override def read(json: JsValue): LocalDateTime = json match {
      case JsString(s) => LocalDateTime.parse(s)
      case _           => throw new DeserializationException("Expected Date time as String in YYYY-mm-dd hh:mm:ss format")
    }
  }

  implicit object UUIDJsonFormat extends RootJsonFormat[UUID] {

    override def write(obj: UUID) = JsString(obj.toString)

    override def read(json: JsValue): UUID = json match {
      case JsString(s) => UUID.fromString(s)
      case _           => throw new DeserializationException("Expected UUID as String")
    }
  }

  implicit val deputyFormat = jsonFormat4(Deputy.apply)
  implicit val contactRequestFormat = jsonFormat7(PostContact.apply)
  implicit val contactResponseFormat = jsonFormat11(GetContact.apply)

  implicit val contactCreateResponseFormat = jsonFormat1(PostContactResponse.apply)
  implicit val sseUpdatedFormat = jsonFormat1(Updated.apply)
  implicit val isImportantFormat = jsonFormat1(IsImportant.apply)

  implicit val contactCreatedMarshaller: ToEntityMarshaller[UUID] = Marshaller.oneOf(
    Marshaller.StringMarshaller.wrap(MediaTypes.`text/plain`)(uuid => uuid.toString),
    Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`)(uuid => s"""{"id":"${uuid}"}""")
  )

  implicit val rawUUIDEntityUnmarshaller: FromEntityUnmarshaller[UUID] =
    PredefinedFromEntityUnmarshallers.stringUnmarshaller.map(UUID.fromString(_))

}

object DomofonMarshalling extends DomofonMarshalling
