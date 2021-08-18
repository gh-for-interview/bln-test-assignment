package assignment

import assignment.queue.MessageProducer
import org.apache.kafka.clients.producer.ProducerRecord

class MessageProducerStub extends MessageProducer {
  val modifyQueue = collection.mutable.Queue.empty[ProducerRecord[_, _]]
  val newQueue = collection.mutable.Queue.empty[ProducerRecord[_, _]]
  val deleteQueue = collection.mutable.Queue.empty[ProducerRecord[_, _]]

  def send(record: ProducerRecord[_, _]): Unit = {
    record.topic match {
      case "new" => newQueue.addOne(record)
      case "modify" => modifyQueue.addOne(record)
      case "delete" => deleteQueue.addOne(record)
      case t => throw new IllegalArgumentException(s"invalid topic $t")
    }
  }
}
