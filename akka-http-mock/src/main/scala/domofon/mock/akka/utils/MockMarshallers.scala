package domofon.mock.akka.utils

import java.time.format.DateTimeParseException
import java.time.{LocalDate, LocalDateTime}
import java.util.UUID

import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.unmarshalling.{Unmarshaller, FromStringUnmarshaller, FromEntityUnmarshaller, PredefinedFromEntityUnmarshallers}
import akka.http.scaladsl.util.FastFuture
import akka.stream.Materializer
import domofon.mock.akka.entities._
import spray.json._

import scala.concurrent.{Future, ExecutionContext}
import scala.util.Try

trait MockMarshallers extends DefaultJsonProtocol {

  implicit val localDateJsonWriter = new JsonFormat[LocalDate] {
    override def read(json: JsValue): LocalDate = {
      val JsString(s) = json
      try {
        LocalDate.parse(s)
      } catch {
        case ex: DateTimeParseException =>
          throw new DeserializationException(msg = ex.getMessage)
      }
    }

    override def write(obj: LocalDate) = JsString(obj.toString)
  }

  implicit val localDateTimeJsonWriter = lift(new JsonWriter[LocalDateTime] {
    override def write(obj: LocalDateTime) = JsString(obj.toString)
  })

  implicit val uuidJsonFormat = new JsonFormat[UUID] {
    override def read(json: JsValue): UUID = (json: @unchecked) match {
      case JsString(s) => UUID.fromString(s)
    }
    override def write(obj: UUID): JsValue = JsString(obj.toString)
  }

  implicit val deputyFormat = jsonFormat4(Deputy.apply)
  implicit val contactRequestFormat = new RootJsonFormat[ContactRequest] {
    private[this] val autoFormat = jsonFormat8(ContactRequest.apply)

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

  implicit val categoryRequestFormat = new RootJsonFormat[CategoryRequest] {
    private[this] val autoFormat = jsonFormat4(CategoryRequest.apply)

    override def write(obj: CategoryRequest): JsValue = obj.toJson(autoFormat)

    override def read(json: JsValue): CategoryRequest = json match {
      case JsObject(fields) =>
        val keys = fields.keySet
        val missing = CategoryRequest.requiredFields -- keys
        if (missing.isEmpty)
          json.convertTo[CategoryRequest](autoFormat)
        else {
          throw new DeserializationException(s"Category doesn't have required fields: ${missing.mkString(", ")}", fieldNames = missing.toList)
        }
      case _ => throw new DeserializationException("Category request must be JSON object")
    }
  }

  implicit val categoryResponseFormat = jsonFormat6(CategoryResponse.apply)

  implicit val sseUpdatedFormat = jsonFormat1(SseUpdated.apply)
  implicit val isImportantFormat = jsonFormat1(IsImportant.apply)
  implicit val missingFieldsErrorFormat = jsonFormat2(MissingFieldsError.apply)
  implicit val tooManyRequestsErrorFormat = jsonFormat2(TooManyRequestsError.apply)

  implicit val contactPublicResponseWriter = new JsonWriter[ContactResponse] {
    private[this] val contatResponseHiddenFields = Seq("secret")
    private[this] val autoFormat = jsonFormat13(ContactResponse.apply)
    override def write(obj: ContactResponse): JsValue = {
      val json = obj.toJson(autoFormat).asJsObject
      JsObject(json.fields -- contatResponseHiddenFields)
    }
  }

  implicit val uuidFromString: FromStringUnmarshaller[UUID] = new Unmarshaller[String, UUID] {
    override def apply(value: String)(
      implicit
      ec: ExecutionContext, materializer: Materializer
    ): Future[UUID] = FastFuture.apply(Try(UUID.fromString(value)))
  }

  implicit val missingFieldsErrorMarshaller: ToEntityMarshaller[MissingFieldsError] = Marshaller.oneOf(
    Marshaller.StringMarshaller.wrap(MediaTypes.`text/plain`)(e => e.message),
    Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`)(e => e.toJson.prettyPrint)
  )

  implicit val tooManyRequestsErrorMarshaller: ToEntityMarshaller[TooManyRequestsError] = Marshaller.oneOf(
    Marshaller.StringMarshaller.wrap(MediaTypes.`text/plain`)(e => e.message),
    Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`)(e => e.toJson.prettyPrint)
  )

  implicit val categoryIsIndividualErrorMarshaller: ToEntityMarshaller[CategoryIsIndividualError.type] = {
    val msg = "Category is Individual, you must send notifications to individual Contacts"
    Marshaller.oneOf(
      Marshaller.StringMarshaller.wrap(MediaTypes.`text/plain`)(e => msg),
      Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`)(e => JsObject(("error", JsString(msg))).prettyPrint)
    )
  }

  implicit val categoryDoesNotExistErrorMarshaller: ToEntityMarshaller[CategoryDoesNotExistError] = {
    def msg(cid: UUID) = s"Category ${cid} does not exist"
    Marshaller.oneOf(
      Marshaller.StringMarshaller.wrap(MediaTypes.`text/plain`)(e => msg(e.categoryId)),
      Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`)(e =>
        JsObject(("error", JsString(msg(e.categoryId))), ("category", JsString(e.categoryId.toString))).prettyPrint)
    )
  }

  implicit val contactCreatedMarshaller: ToEntityMarshaller[ContactCreated] = Marshaller.oneOf(
    Marshaller.StringMarshaller.wrap(MediaTypes.`text/plain`)(cc => cc.id.toString),
    Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`)(cc => JsObject(
      ("id", JsString(cc.id.toString)), ("secret", JsString(cc.secret.toString))
    ).prettyPrint)
  )

  implicit val categoryCreatedMarshaller: ToEntityMarshaller[CategoryCreated] = Marshaller.oneOf(
    Marshaller.StringMarshaller.wrap(MediaTypes.`text/plain`)(cc => cc.id.toString),
    Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`)(cc => JsObject(
      ("id", JsString(cc.id.toString))
    ).prettyPrint)
  )

  implicit val loginTokenMarshaller: ToEntityMarshaller[LoginToken] = Marshaller.oneOf(
    Marshaller.StringMarshaller.wrap(MediaTypes.`text/plain`)(lt => lt.token.toString),
    Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`)(lt => JsObject(
      ("token", JsString(lt.token.toString))
    ).prettyPrint)
  )

  implicit val operationResultMarshaller: ToEntityMarshaller[OperationResult] = Marshaller.oneOf(
    Marshaller.StringMarshaller.wrap(MediaTypes.`text/plain`)(r => r.status),
    Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`)(r => JsObject(("status", JsString(r.status))).prettyPrint)
  )

  implicit val validationErrorFormat = jsonFormat2(ValidationError.apply)

}

object MockMarshallers extends MockMarshallers
