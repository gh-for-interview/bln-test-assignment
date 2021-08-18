package assignment.consumers

import assignment.database.Storage
import assignment.queue.ModifyWebhookMessage
import assignment.queue.MessageSerializers.modifyWebhookMessageFormat
import com.typesafe.scalalogging.LazyLogging
import spray.json.enrichString

import scala.concurrent.{ExecutionContext, Future}


class ModifyWebhookConsumer(storage: Storage)(implicit ec: ExecutionContext) extends LazyLogging {
  def consume(msg: String): Future[_] = Future {
    val message: ModifyWebhookMessage = msg.parseJson.convertTo[ModifyWebhookMessage]
    logger.info(s"ModifyWebhookConsumer received message $message")
    storage.modifyWebhook(message.id, message.maybeUrl, message.maybeEvent)
  }
}
