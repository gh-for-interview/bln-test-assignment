package assignment

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import assignment.http.{AppRouter, IdResponse, ModifyWebhookRequest, NewWebhookRequest, ResponseStatus, WebhookResponse}
import com.typesafe.config.ConfigFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import akka.http.scaladsl.testkit.ScalatestRouteTest
import assignment.http.RequestMarshallers._
import assignment.http.ResponseMarshallers._
import assignment.queue.MessageSerializers._
import assignment.queue.{DeleteWebhookMessage, ModifyWebhookMessage, NewWebhookMessage}
import spray.json.{enrichAny, enrichString}


class RouteSpec extends AnyFlatSpec with Matchers with ScalatestRouteTest {
  val config = ConfigFactory.defaultApplication()
  val messageProducer = new MessageProducerStub

  val route = new AppRouter(config, messageProducer, StorageStub).buildRoute()

  val ct = ContentTypes.`application/json`

  "App route" should "return 404 for non existing webhook" in {
    Get("/api/v1/webhook/non-existing-id") ~> route ~> check {
      status shouldEqual StatusCodes.NotFound
    }
  }

  it should "return webhook" in {
    val webhook = Webhook("https://some-url.com", "creation", "some-id")
    StorageStub.putWebhook(webhook)

    Get(s"/api/v1/webhook/${webhook.id}") ~> route ~> check {
      responseAs[String] shouldBe WebhookResponse(ResponseStatus.Ok, webhook).toJson.prettyPrint
    }
  }

  it should "send new webhook message to queue" in {
    val url = "https://some-url.com"
    val event = "creation"
    var id = ""


    Put(s"/api/v1/webhook/", HttpEntity(ct, NewWebhookRequest(url, event).toJson.compactPrint)) ~> route ~> check {
      id = responseAs[String].parseJson.convertTo[IdResponse].id
    }

    messageProducer.newQueue.head.value shouldBe NewWebhookMessage(Webhook(url, event, id)).toJson.compactPrint
  }

  it should "send modify webhook message to queue" in {
    val url = "https://some-url.com"
    val event = "creation"
    val id = "some-id"

    Patch(s"/api/v1/webhook/$id", HttpEntity(ct, ModifyWebhookRequest(Some(url), Some(event)).toJson.compactPrint)) ~> route ~> check {
      status shouldEqual StatusCodes.OK
    }

    messageProducer.modifyQueue.head.value shouldBe ModifyWebhookMessage(Some(url), Some(event), id).toJson.compactPrint
  }

  it should "return error for bad request for webhook modification" in {
    val id = "some-id"

    Patch(s"/api/v1/webhook/$id", HttpEntity(ct, ModifyWebhookRequest(None, None).toJson.compactPrint)) ~> route ~> check {
      status shouldEqual StatusCodes.BadRequest
    }
  }

  it should "send delete webhook message to queue" in {
    val id = "some-id"

    Delete(s"/api/v1/webhook/$id") ~> route ~> check {
      status shouldEqual StatusCodes.OK
    }

    messageProducer.deleteQueue.head.value shouldBe DeleteWebhookMessage(id).toJson.compactPrint
  }
}
