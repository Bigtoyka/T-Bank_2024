package org.tbank.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@Warmup(iterations = 1)
@Measurement(iterations = 10)
@State(Scope.Thread)
public class KafkaBenchmarks {
    private List<KafkaProducer<String, String>> producers;
    private List<KafkaConsumer<String, String>> consumers;

    @Param({"simple", "load_balancing", "multiple_consumers", "load_balancing_multiple_consumers", "stress_test"})
    public String configuration;

    @Setup(Level.Trial)
    public void setup() {
        int producerCount = 0;
        int consumerCount = 0;

        switch (configuration) {
            case "simple" -> {
                producerCount = 1;
                consumerCount = 1;
            }
            case "load_balancing" -> {
                producerCount = 3;
                consumerCount = 1;
            }
            case "multiple_consumers" -> {
                producerCount = 1;
                consumerCount = 3;
            }
            case "load_balancing_multiple_consumers" -> {
                producerCount = 3;
                consumerCount = 3;
            }
            case "stress_test" -> {
                producerCount = 10;
                consumerCount = 10;
            }
        }
        producers = new ArrayList<>();
        consumers = new ArrayList<>();
        Properties producerProps = createProducer();
        for (int i = 0; i < producerCount; i++) {
            producers.add(new KafkaProducer<>(producerProps));
        }

        Properties consumerProps = createConsumer();
        for (int i = 0; i < consumerCount; i++) {
            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
            consumer.subscribe(List.of("test-topic"));
            consumers.add(consumer);
        }
    }

    @Benchmark
    public void kafkaBench(Blackhole blackhole) {
        producers.forEach(producer -> {
            String message = "Тестовое сообщение ";
            var sendFuture = producer.send(new ProducerRecord<>("test_topic", "key", message));
            try {
                sendFuture.get(1, TimeUnit.SECONDS);
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при отправке сообщения ", e);
            }
            blackhole.consume(sendFuture);
        });

        consumers.forEach(consumer -> {
            var records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                blackhole.consume(record.value());
            }
        });

        blackhole.consume(producers);
        blackhole.consume(consumers);
    }

    private Properties createProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
        return props;
    }

    private Properties createConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "benchmark_group");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        return props;
    }

    @TearDown(Level.Trial)
    public void teardown() {
        producers.forEach(KafkaProducer::close);
        consumers.forEach(KafkaConsumer::close);
    }
}
