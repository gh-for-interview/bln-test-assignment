package assignment

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class Webhook(url: String, event: String, id: String)

object WebhookFormatter extends DefaultJsonProtocol {
  implicit val webhookFormat: RootJsonFormat[Webhook] = jsonFormat3(Webhook)
}
