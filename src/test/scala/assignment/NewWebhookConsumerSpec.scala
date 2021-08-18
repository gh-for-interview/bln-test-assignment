package assignment

import assignment.consumers.NewWebhookConsumer
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import spray.json.enrichAny
import assignment.queue.MessageSerializers._
import assignment.queue.NewWebhookMessage

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

class NewWebhookConsumerSpec extends AnyFlatSpec with Matchers {

  val consumer = new NewWebhookConsumer(StorageStub)

  "NewWebhookConsumer" should "save webhook" in {
    val webhook = Webhook("http://some-example.com", "creation", "some-id")

    val f = consumer.consume(NewWebhookMessage(webhook).toJson.compactPrint)
    Await.ready(f, 5.seconds)

    StorageStub.getWebhook(webhook.id) shouldBe Some(webhook)
  }
}
