package domofon.tck

import java.time.{LocalDate, LocalDateTime}
import java.util.UUID

import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, PredefinedFromEntityUnmarshallers}
import domofon.tck.entities._
import spray.json._

trait DomofonMarshalling extends DefaultJsonProtocol {

  implicit val localDateJsonFormat = lift(new JsonReader[LocalDate] {
    override def read(json: JsValue): LocalDate = (json: @unchecked) match {
      case JsString(s) => LocalDate.parse(s)
    }
  })

  implicit val localDateTimeJsonFormat = lift(new JsonReader[LocalDateTime] {
    override def read(json: JsValue): LocalDateTime = (json: @unchecked) match {
      case JsString(s) => LocalDateTime.parse(s)
    }
  })

  implicit val uuidJsonFormat = lift(new JsonReader[UUID] {
    override def read(json: JsValue): UUID = (json: @unchecked) match {
      case JsString(s) => UUID.fromString(s)
    }
  })

  implicit val notificationRetryFormat = jsonFormat2(NotificationRetry.apply)
  implicit val deputyFormat = jsonFormat4(Deputy.apply)
  implicit val contactRequestFormat = jsonFormat7(PostContact.apply)
  implicit val contactResponseFormat = jsonFormat11(GetContact.apply)

  implicit val contactCreateResponseFormat = jsonFormat1(PostContactResponse.apply)
  implicit val sseUpdatedFormat = jsonFormat1(Updated.apply)
  implicit val isImportantFormat = jsonFormat1(IsImportant.apply)

  implicit val contactCreatedMarshaller: ToEntityMarshaller[UUID] = Marshaller.oneOf(
    Marshaller.StringMarshaller.wrap(MediaTypes.`text/plain`)(uuid => uuid.toString),
    Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`)(uuid => JsObject(("id", JsString(uuid.toString))).prettyPrint)
  )

  implicit val validationFieldsError = jsonFormat2(ValidationFieldsError.apply)

  implicit val rawUUIDEntityUnmarshaller: FromEntityUnmarshaller[UUID] =
    PredefinedFromEntityUnmarshallers.stringUnmarshaller.map(UUID.fromString(_))

}

object DomofonMarshalling extends DomofonMarshalling
