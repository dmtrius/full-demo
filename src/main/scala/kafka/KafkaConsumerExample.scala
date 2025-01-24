package kafka

import java.time.Duration
import java.util.{Collections, Properties}
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer, ConsumerRecords}
import org.apache.kafka.common.serialization.StringDeserializer

@main def KafkaConsumerExample(): Unit =
  val props = new Properties()
  props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group")
  props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
  props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)

  val consumer = new KafkaConsumer[String, String](props)
  consumer.subscribe(Collections.singletonList("my-super-topic"))

  while (true) {
    val records: ConsumerRecords[String, String] = consumer.poll(Duration.ofMillis(100))
    records.forEach(record => {
      println(s"Received message: key = ${record.key()}, value = ${record.value()}, offset = ${record.offset()}")
    })
  }

  consumer.close()
