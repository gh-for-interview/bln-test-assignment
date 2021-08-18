package assignment

import assignment.consumers.DeleteWebhookConsumer
import assignment.queue.MessageSerializers._
import assignment.queue.DeleteWebhookMessage
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import spray.json.enrichAny

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

class DeleteWebhookConsumerSpec extends AnyFlatSpec with Matchers {

  val consumer = new DeleteWebhookConsumer(StorageStub)

  "DeleteWebhookConsumer" should "delete webhook" in {
    val webhook = Webhook("http://some-example.com", "creation", "some-id")
    StorageStub.putWebhook(webhook)

    val f = consumer.consume(DeleteWebhookMessage("some-id").toJson.compactPrint)
    Await.ready(f, 5.seconds)

    StorageStub.getWebhook(webhook.id) shouldBe None
  }
}
