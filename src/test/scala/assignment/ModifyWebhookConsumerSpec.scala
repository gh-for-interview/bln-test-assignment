package assignment

import assignment.consumers.ModifyWebhookConsumer
import assignment.queue.MessageSerializers._
import assignment.queue.ModifyWebhookMessage
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import spray.json.enrichAny

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

class ModifyWebhookConsumerSpec extends AnyFlatSpec with Matchers {

  val consumer = new ModifyWebhookConsumer(StorageStub)

  "ModifyWebhookConsumer" should "modify webhook" in {
    val webhook = Webhook("http://some-example.com", "creation", "some-id")
    StorageStub.putWebhook(webhook)

    val newUrl = "http://other-example.com"
    val newEvent = "deletion"

    val f = consumer.consume(ModifyWebhookMessage(Some(newUrl), Some(newEvent), "some-id").toJson.compactPrint)
    Await.ready(f, 5.seconds)

    StorageStub.getWebhook(webhook.id) shouldBe Some(Webhook(newUrl, newEvent, "some-id"))
  }
}
