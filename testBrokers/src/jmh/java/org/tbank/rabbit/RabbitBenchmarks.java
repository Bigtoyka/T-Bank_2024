package org.tbank.rabbit;


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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 1)
@Measurement(iterations = 3)
@State(Scope.Thread)
public class RabbitBenchmarks {
    private List<RabbitProducer> producers;
    private List<RabbitConsumer> consumers;

    @Param({"simple", "load_balancing", "multiple_consumers", "load_balancing_multiple_consumers", "stress_test"})
    private String configuration;


    @Setup(Level.Trial)
    public void setup() throws IOException, TimeoutException {
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
        for (int i = 0; i < producerCount; i++) {
            producers.add(new RabbitProducer());
        }
        for (int i = 0; i < consumerCount; i++) {
            consumers.add(new RabbitConsumer());
        }
    }

    @Benchmark
    public void rabbitBench(Blackhole blackhole) {
        producers.forEach(producer -> {
            try {
                String message = "Тестовое сообщение ";
                producer.sendMessage(message);
                blackhole.consume(message);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка ", e);
            }
            blackhole.consume(producer);
        });
        consumers.forEach(consumer -> {
            try {
                String message = consumer.getMessage();
                blackhole.consume(message);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка ", e);
            }
            blackhole.consume(consumer);
        });
        blackhole.consume(producers);
        blackhole.consume(consumers);
    }

    @TearDown(Level.Trial)
    public void teardown() {
        producers.forEach(RabbitProducer::close);
        consumers.forEach(RabbitConsumer::close);
    }

}
