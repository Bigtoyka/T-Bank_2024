import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class RabbitTestLoadBalancing {
    private Connection connection;
    private Channel[] producerChannels;
    private Channel consumerChannel;
    private static final String EXCHANGE = "test-exchange";
    private static final String QUEUE = "test-queue";
    private static final String ROUTING_KEY = "test-key";
    private static final int PRODUCER_COUNT = 3;

    @Setup(Level.Trial)
    public void setUp() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();

        producerChannels = new Channel[PRODUCER_COUNT];
        for (int i = 0; i < PRODUCER_COUNT; i++) {
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.DIRECT, true);
            channel.queueDeclare(QUEUE, true, false, false, null);
            channel.queueBind(QUEUE, EXCHANGE, ROUTING_KEY);
            producerChannels[i] = channel;
        }

        consumerChannel = connection.createChannel();
        consumerChannel.basicQos(1);

        consumerChannel.basicConsume(QUEUE, true, (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        }, consumerTag -> {
        });
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException, TimeoutException {
        for (Channel producerChannel : producerChannels) {
            if (producerChannel != null && producerChannel.isOpen()) {
                producerChannel.close();
            }
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
        String message = "LOAD BALANCING TEST";
        for (Channel producerChannel : producerChannels) {
            producerChannel.basicPublish(EXCHANGE, ROUTING_KEY, null, message.getBytes(StandardCharsets.UTF_8));
        }
    }
}
