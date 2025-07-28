package com.example.demo.apps.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Properties;

import static java.io.IO.println;

@Slf4j
public class WordCountExample {
    public static final String INPUT_TOPIC = "input-topic";
    public static final String OUTPUT_TOPIC = "output-topic";

    void main() {
        streams();
    }

    @SuppressWarnings("preview")
    private static void streams() {
        // Stream builder
        StreamsBuilder builder = new StreamsBuilder();

        // Read input stream from the Kafka topic
        KStream<String, String> textLines = builder.stream(INPUT_TOPIC);

        // Word count logic
        KTable<String, Long> wordCounts = textLines
                .peek((key, word) -> println("### Received key: " + key + ", word: " + word))
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
                .groupBy((key, word) -> word)
                .count();

        // Write word counts to the output topic
        wordCounts.toStream()
                .peek((k, v) -> println("### WordCount: " + k + " -> " + v))
                .to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.Long()));

        // Start the stream
        KafkaStreams streams = new KafkaStreams(builder.build(), getConfig());
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private static Properties getConfig()  {
        // Kafka Streams configuration
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.AT_LEAST_ONCE);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConfig.GROUP);
        props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, true);

        return props;
    }
}
