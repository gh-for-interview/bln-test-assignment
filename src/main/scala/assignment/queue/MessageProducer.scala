package assignment.queue

import org.apache.kafka.clients.producer.ProducerRecord

trait MessageProducer {
  def send(record: ProducerRecord[_, _]): Unit
}
