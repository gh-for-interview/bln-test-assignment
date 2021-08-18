package assignment.http

import spray.json.{DefaultJsonProtocol, RootJsonFormat}


case class NewWebhookRequest(url: String, event: String)

case class ModifyWebhookRequest(url: Option[String], event: Option[String])

object RequestMarshallers extends DefaultJsonProtocol {
  implicit val newWebhookRequestFormat: RootJsonFormat[NewWebhookRequest] = jsonFormat2(NewWebhookRequest)
  implicit val modifyWebhookRequestFormat: RootJsonFormat[ModifyWebhookRequest] = jsonFormat2(ModifyWebhookRequest)
}
