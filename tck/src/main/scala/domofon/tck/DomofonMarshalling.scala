package domofon.tck

import java.time.{LocalDate, LocalDateTime}

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

  implicit val deputyFormat = jsonFormat4(Deputy.apply)
  implicit val contactRequestFormat = jsonFormat8(PostContact.apply)
  implicit val contactResponseFormat = jsonFormat10(GetContact.apply)
  implicit val categoryRequestFormat = jsonFormat4(PostCategory.apply)
  implicit val categoryResponseFormat = jsonFormat5(GetCategory.apply)

  implicit val entityCreatedFormat = jsonFormat1(EntityCreated.apply)
  implicit val entityCreatedWithSecret = jsonFormat2(EntityCreatedWithSecret.apply)
  implicit val notificationRetryFormat = jsonFormat2(NotificationRetry.apply)
  implicit val sseUpdatedFormat = jsonFormat1(Updated.apply)
  implicit val isImportantFormat = jsonFormat1(IsImportant.apply)
  implicit val validationFieldsError = jsonFormat2(ValidationFieldsError.apply)
  implicit val categoryMessageFormat = jsonFormat2(CategoryMessage.apply)

}

object DomofonMarshalling extends DomofonMarshalling
