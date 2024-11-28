import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Benchmark;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class RabbitMQSimple {
    private Connection connection;
    private Channel producerChannel;
    private Channel consumerChannel;
    private static final String EXCHANGE = "test-exchange";
    private static final String QUEUE = "test-queue";
    private static final String ROUTING_KEY = "test-key";

    @Setup(Level.Trial)
    public void setUp() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        producerChannel = connection.createChannel();
        producerChannel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.DIRECT, true);
        producerChannel.queueDeclare(QUEUE, true, false, false, null);
        producerChannel.queueBind(QUEUE, EXCHANGE, ROUTING_KEY);

        consumerChannel = connection.createChannel();
        consumerChannel.basicQos(1);
        consumerChannel.basicConsume(QUEUE, true, (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        }, consumerTag -> {});

    }

    @TearDown
    public void tearDown() throws IOException, TimeoutException {
        if (producerChannel != null && producerChannel.isOpen()) {
            producerChannel.close();
        }
        if (consumerChannel != null && consumerChannel.isOpen()) {
            consumerChannel.close();
        }
        if (connection != null && connection.isOpen()) {
            connection.close();
        }
    }

    @Benchmark
    public void testProducerConsumer() throws IOException {
        String msg = "SIMPLE-TEST";
        producerChannel.basicPublish(EXCHANGE, ROUTING_KEY, null, msg.getBytes(StandardCharsets.UTF_8));
    }
}
