package assignment.http

import java.util.UUID

import akka.event.Logging.InfoLevel
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import assignment.queue.MessageSerializers._
import assignment.http.RequestMarshallers._
import com.typesafe.config.Config
import org.apache.kafka.clients.producer.ProducerRecord
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.directives.LogEntry
import assignment.Webhook
import assignment.database.Storage
import assignment.queue.{DeleteWebhookMessage, MessageProducer, ModifyWebhookMessage, NewWebhookMessage}
import spray.json.enrichAny
import assignment.http.ResponseMarshallers._


class AppRouter(config: Config, messageProducer: MessageProducer, storage: Storage) {

  private val topicConfig = config.getConfig("app.topic")

  private val newWebhookTopic = topicConfig.getString("new-webhook")
  private val changeWebhookTopic = topicConfig.getString("change-webhook")
  private val deleteWebhookTopic = topicConfig.getString("delete-webhook")

  private def extractRequestInfoForLogging(req: HttpRequest) = {
    val message = s"${req.method.value} ${req.uri}"
    LogEntry(message, InfoLevel)
  }

  def buildRoute(): Route = {
    logRequest(extractRequestInfoForLogging(_)) {
      pathPrefix("api" / "v1") {
        path("webhook" / Segment.?) {
          case Some(id) =>
            delete {
              val message = DeleteWebhookMessage(id)
              val bodyString = message.toJson.compactPrint
              val record = new ProducerRecord[String, String](deleteWebhookTopic, bodyString)

              messageProducer.send(record)
              complete(StatusCodes.OK, HttpEntity(GenericResponse(ResponseStatus.Ok).toJson.compactPrint))
            } ~ get {
              storage.getWebhook(id) match {
                case Some(webhook) =>
                  val message = WebhookResponse(ResponseStatus.Ok, webhook)
                  complete(StatusCodes.OK, HttpEntity(message.toJson.prettyPrint))
                case None =>
                  val message = GenericResponse(ResponseStatus.Error, Some(s"Webhook with id $id not found"))
                  complete(StatusCodes.NotFound, HttpEntity(message.toJson.prettyPrint))
              }
            } ~ patch {
              entity(as[ModifyWebhookRequest]) { entity =>
                if (entity.event.isEmpty && entity.url.isEmpty) {
                  val errorMessage = "Both url and event cannot be empty"
                  val message = GenericResponse(ResponseStatus.Error, Some(errorMessage))
                  complete(StatusCodes.BadRequest, HttpEntity(message.toJson.prettyPrint))
                } else {
                  val message = ModifyWebhookMessage(entity.url, entity.event, id)
                  val bodyString = message.toJson.compactPrint
                  val record = new ProducerRecord[String, String](changeWebhookTopic, bodyString)

                  messageProducer.send(record)
                  val responseMessage = GenericResponse(ResponseStatus.Ok)
                  complete(StatusCodes.OK, HttpEntity(responseMessage.toJson.prettyPrint))
                }
              }
            }
          case None =>
            put {
              entity(as[NewWebhookRequest]) { request =>
                val id = UUID.randomUUID().toString
                val webhook = Webhook(request.url, request.event, id)
                val bodyString = NewWebhookMessage(webhook).toJson.compactPrint
                val record = new ProducerRecord[String, String](newWebhookTopic, bodyString)

                messageProducer.send(record)

                val responseMessage = IdResponse(ResponseStatus.Ok, id)
                complete(StatusCodes.OK, HttpEntity(responseMessage.toJson.prettyPrint))
              }
            }
        }
      }
    }
  }
}
