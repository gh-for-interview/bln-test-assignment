package assignment.consumers

import assignment.database.Storage
import assignment.queue.MessageSerializers.deleteWebhookMessageFormat
import assignment.queue.DeleteWebhookMessage
import com.typesafe.scalalogging.LazyLogging
import spray.json.enrichString

import scala.concurrent.{ExecutionContext, Future}


class DeleteWebhookConsumer(storage: Storage)(implicit ec: ExecutionContext) extends LazyLogging {
  def consume(msg: String): Future[_] = Future {
    val message: DeleteWebhookMessage = msg.parseJson.convertTo[DeleteWebhookMessage]
    logger.info(s"DeleteWebhookConsumer received message $message")
    storage.deleteWebhook(message.id)
  }
}
