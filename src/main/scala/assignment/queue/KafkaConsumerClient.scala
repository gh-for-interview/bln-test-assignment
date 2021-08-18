package assignment.queue

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.kafka.scaladsl.{Committer, Consumer}
import akka.kafka.{CommitterSettings, ConsumerSettings, Subscriptions}
import akka.stream.Materializer
import com.typesafe.config.Config
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.{ExecutionContext, Future}


class KafkaConsumerClient(config: Config, topic: String, f: String => Future[_])
                         (implicit ec: ExecutionContext, m: Materializer, as: ActorSystem) {

  private val consumerSettings = ConsumerSettings(as, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(config.getString("bootstrap-servers"))
      .withGroupId("group1")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  private val control = Consumer
      .committableSource(consumerSettings, Subscriptions.topics(topic))
      .mapAsync(1) { msg => f(msg.record.value).map(_ => msg.committableOffset) }
      .toMat(Committer.sink(CommitterSettings(config.getConfig("commiter-settings"))))(DrainingControl.apply)

  def run(): DrainingControl[Done] = control.run()
}
