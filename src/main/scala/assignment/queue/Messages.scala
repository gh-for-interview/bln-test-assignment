package assignment.queue

import assignment.Webhook
import spray.json.{DefaultJsonProtocol, RootJsonFormat}


case class NewWebhookMessage(webhook: Webhook)

case class DeleteWebhookMessage(id: String)

case class ModifyWebhookMessage(maybeUrl: Option[String], maybeEvent: Option[String], id: String)

object MessageSerializers extends DefaultJsonProtocol {
  import assignment.WebhookFormatter._

  implicit val newWebhookMessageFormat: RootJsonFormat[NewWebhookMessage] = jsonFormat1(NewWebhookMessage)
  implicit val deleteWebhookMessageFormat: RootJsonFormat[DeleteWebhookMessage] = jsonFormat1(DeleteWebhookMessage)
  implicit val modifyWebhookMessageFormat: RootJsonFormat[ModifyWebhookMessage] = jsonFormat3(ModifyWebhookMessage)
}
