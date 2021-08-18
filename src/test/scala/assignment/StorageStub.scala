package assignment

import assignment.database.Storage

object StorageStub extends Storage {
  private val storage = collection.mutable.Map.empty[String, Webhook]

  def putWebhook(wh: Webhook): Unit = storage.addOne(wh.id -> wh)

  def deleteWebhook(id: String): Unit = storage.remove(id)

  def getWebhook(id: String): Option[Webhook] = storage.get(id)

  def modifyWebhook(id: String, maybeUrl: Option[String], maybeEvent: Option[String]): Unit = {
    storage.get(id) match {
      case Some(webhook) =>
        storage.addOne(id -> webhook.copy(url = maybeUrl.getOrElse(webhook.url), event = maybeEvent.getOrElse(webhook.event)))
      case None =>
    }
  }
}
