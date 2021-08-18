package assignment.consumers

import assignment.database.Storage
import assignment.queue.NewWebhookMessage
import spray.json.enrichString
import assignment.queue.MessageSerializers.newWebhookMessageFormat
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}


class NewWebhookConsumer(storage: Storage)(implicit ec: ExecutionContext) extends LazyLogging {
  def consume(msg: String): Future[_] = Future {
    val message: NewWebhookMessage = msg.parseJson.convertTo[NewWebhookMessage]
    logger.info(s"NewWebhookConsumer received message $message")
    storage.putWebhook(message.webhook)
  }
}
