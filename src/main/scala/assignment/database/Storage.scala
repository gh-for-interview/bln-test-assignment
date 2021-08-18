package assignment.database

import assignment.Webhook

trait Storage {
  def putWebhook(wh: Webhook): Unit
  def deleteWebhook(id: String): Unit
  def getWebhook(id: String): Option[Webhook]
  def modifyWebhook(id: String, maybeUrl: Option[String], maybeEvent: Option[String]): Unit
}
