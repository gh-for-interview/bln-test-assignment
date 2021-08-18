package assignment.queue

import java.util.Properties

import com.typesafe.config.Config
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

import scala.jdk.CollectionConverters._


class KafkaProducerClient(config: Config) extends MessageProducer {

  private val props = new Properties()
  config.entrySet().asScala.foreach { entity =>
    props.setProperty(entity.getKey.stripPrefix("\"").stripSuffix("\""), config.getString(entity.getKey))
  }

  private val client = new KafkaProducer[String, String](props, new StringSerializer(), new StringSerializer())

  def send(record: ProducerRecord[_, _]): Unit = client.send(record.asInstanceOf[ProducerRecord[String, String]])
}
