package domofon.mock.akka

import java.time.{LocalDate, LocalDateTime}
import java.util.UUID

import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, PredefinedFromEntityUnmarshallers}
import spray.json._

trait MockMarshallers extends DefaultJsonProtocol {

  implicit val localDateJsonWriter = lift(new JsonWriter[LocalDate] {

    override def write(obj: LocalDate) = JsString(obj.toString)
  })

  implicit val localDateTimeJsonWriter = lift(new JsonWriter[LocalDateTime] {
    override def write(obj: LocalDateTime) = JsString(obj.toString)
  })

  implicit val uuidJsonWriter = lift(new JsonWriter[UUID] {
    override def write(obj: UUID) = JsString(obj.toString)
  })

  implicit val deputyFormat = jsonFormat4(Deputy.apply)
  implicit val contactRequestFormat = new RootJsonFormat[ContactRequest] {
    private[this] val autoFormat = jsonFormat7(ContactRequest.apply)

    override def write(obj: ContactRequest): JsValue = obj.toJson(autoFormat)

    override def read(json: JsValue): ContactRequest = json match {
      case JsObject(fields) =>
        val keys = fields.keySet
        val missing = ContactRequest.requiredFields -- keys
        if (missing.isEmpty)
          json.convertTo[ContactRequest](autoFormat)
        else {
          throw new DeserializationException(s"Contact doesn't have required fields: ${missing.mkString(", ")}", fieldNames = missing.toList)
        }
      case _ => throw new DeserializationException("Contact request must be JSON object")
    }
  }

  implicit val contactResponseFormat = jsonFormat12(ContactResponse.apply)

  implicit val contactCreateResponseFormat = jsonFormat1(ContactCreateResponse.apply)
  implicit val sseUpdatedFormat = jsonFormat1(SseUpdated.apply)
  implicit val isImportantFormat = jsonFormat1(IsImportant.apply)
  implicit val missingFieldsErrorFormat = jsonFormat2(MissingFieldsError.apply)
  implicit val tooManyRequestsErrorFormat = jsonFormat2(TooManyRequestsError.apply)
  implicit val contactMessageUpdatedFormat = jsonFormat1(ContactMessageUpdated.apply)

  val contactWithoutMessageWriter = new JsonWriter[ContactResponse] {
    override def write(obj: ContactResponse): JsValue = {
      val json = obj.toJson(contactResponseFormat).asJsObject
      JsObject(json.fields.filterKeys(_ != "message"))
    }
  }

  implicit val contactCreatedMarshaller: ToEntityMarshaller[UUID] = Marshaller.oneOf(
    Marshaller.StringMarshaller.wrap(MediaTypes.`text/plain`)(uuid => uuid.toString),
    Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`)(uuid => s"""{"id":"${uuid}"}""")
  )

  implicit val missingFieldsErrorMarshaller: ToEntityMarshaller[MissingFieldsError] = Marshaller.oneOf(
    Marshaller.StringMarshaller.wrap(MediaTypes.`text/plain`)(e => e.message),
    Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`)(e => e.toJson.prettyPrint)
  )

  implicit val tooManyRequestsErrorMarshaller: ToEntityMarshaller[TooManyRequestsError] = Marshaller.oneOf(
    Marshaller.StringMarshaller.wrap(MediaTypes.`text/plain`)(e => e.message),
    Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`)(e => e.toJson.prettyPrint)
  )

  implicit val rawUUIDEntityUnmarshaller: FromEntityUnmarshaller[UUID] =
    PredefinedFromEntityUnmarshallers.stringUnmarshaller.map(UUID.fromString(_))

  implicit val contactMessageUpdatedMarshaller: ToEntityMarshaller[ContactMessageUpdated] = Marshaller.oneOf(
    Marshaller.StringMarshaller.wrap(MediaTypes.`text/plain`)(c => c.status),
    Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`)(c => c.toJson.prettyPrint)
  )

  implicit val validationErrorFormat = jsonFormat2(ValidationError.apply)

}

object MockMarshallers extends MockMarshallers
