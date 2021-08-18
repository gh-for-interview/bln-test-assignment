package assignment


import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import assignment.consumers.{DeleteWebhookConsumer, ModifyWebhookConsumer, NewWebhookConsumer}
import assignment.database.DBClient
import assignment.http.AppRouter
import assignment.queue.{KafkaConsumerClient, KafkaProducerClient}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}


object Main extends App with StrictLogging {
  val config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem("App", config)
  implicit val ec: ExecutionContext = system.dispatcher

  val router = new AppRouter(config, new KafkaProducerClient(config.getConfig("kafka.producer.main.props")), DBClient)

  val kafkaConfig = config.getConfig("kafka.consumer.default")

  new KafkaConsumerClient(
    kafkaConfig,
    config.getString("app.topic.new-webhook"),
    x => new NewWebhookConsumer(DBClient).consume(x)
  ).run()

  new KafkaConsumerClient(
    kafkaConfig,
    config.getString("app.topic.delete-webhook"),
    x => new DeleteWebhookConsumer(DBClient).consume(x)
  ).run()

  new KafkaConsumerClient(
    kafkaConfig,
    config.getString("app.topic.change-webhook"),
    x => new ModifyWebhookConsumer(DBClient).consume(x)
  ).run()

  Http()
    .bindAndHandle(
      router.buildRoute(),
      interface = config.getString("app.api.interface"),
      port = config.getInt("app.api.port")
    )
    .onComplete {
      case Success(serverBinding) => logger.info(s"Bound app HTTP API to {}", serverBinding.localAddress)
      case Failure(e) => logger.error(s"Unable to bound app HTTP API", e)
    }
}
