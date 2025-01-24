package kafka

import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

@main def KafkaProducerExample(): Unit =
  val props = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)

  val topic = "my-super-topic"
  val key = "key1"
  val value = "Hello, Kafka!"

  val record = new ProducerRecord[String, String](topic, key, value)

  producer.send(record, (metadata, exception) => {
    if (exception == null) {
      println(s"Sent message to topic:${metadata.topic()} partition:${metadata.partition()} offset:${metadata.offset()}")
    } else {
      exception.printStackTrace()
    }
  })

  producer.close()
