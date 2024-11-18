package org.tbank;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.openjdk.jmh.annotations.*;
import org.tbank.brokers.RabbitConsumer;
import org.tbank.brokers.RabbitProducer;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class RabbitMQProducerConsumerBenchmark {
    private static final String QUEUE_NAME = "testQueue";
    private static final String RABBITMQ_HOST = "localhost";
    private static final int MESSAGE_COUNT = 1000;
    private RabbitProducer producer;
    private RabbitConsumer consumer;
    private Connection connection;
    private Channel channel;


    @Setup(Level.Trial)
    public void setup() throws Exception {
        producer = new RabbitProducer();
        consumer = new RabbitConsumer();
// Установка соединения с RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RABBITMQ_HOST);
        connection = factory.newConnection();
        channel = connection.createChannel();

        // Создаем очередь для тестов
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    }

    @Benchmark
    public void testProducerConsumer() throws Exception {
        // Отправка сообщений (Продюсер)
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            producer.sendMessage("Message " + i);
        }

        // Получение сообщений (Консюмер)
        consumer.consumeMessages();
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception{
        // Закрытие соединения и канала после теста
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
        if (connection != null && connection.isOpen()) {
            connection.close();
        }
    }
}
