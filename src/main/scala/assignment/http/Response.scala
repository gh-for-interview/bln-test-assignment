package assignment.http

import assignment.Webhook
import assignment.WebhookFormatter.webhookFormat
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

case class GenericResponse(status: ResponseStatus.Value, maybeErrorMessage: Option[String] = None)

case class IdResponse(status: ResponseStatus.Value, id: String)

case class WebhookResponse(status: ResponseStatus.Value, webhook: Webhook)

object ResponseStatus extends Enumeration {
  val Ok, Error = Value

  private val index = values.map(v => v.toString -> v).toMap

  def byStatus(status: String): Option[Value] = index.get(status)
}

object ResponseMarshallers extends DefaultJsonProtocol {
  implicit object responseStatusFormat extends RootJsonFormat[ResponseStatus.Value] {
    override def write(obj: ResponseStatus.Value): JsValue = {
      JsString(obj.toString)
    }

    override def read(json: JsValue): ResponseStatus.Value = {
      json match {
        case JsString(status) => ResponseStatus
          .byStatus(status)
          .getOrElse(throw DeserializationException(s"invalid status $status"))
        case _ => throw DeserializationException(s"invalid status $json")
      }
    }
  }

  implicit val genericResponseFormat: RootJsonFormat[GenericResponse] = jsonFormat2(GenericResponse)
  implicit val idResponseFormat: RootJsonFormat[IdResponse] = jsonFormat2(IdResponse)
  implicit val webhookResponseFormat: RootJsonFormat[WebhookResponse] = jsonFormat2(WebhookResponse)
}
