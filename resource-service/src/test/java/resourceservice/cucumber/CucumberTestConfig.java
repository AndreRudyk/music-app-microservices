package resourceservice.cucumber;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@TestConfiguration
@Testcontainers
public class CucumberTestConfig {

    private static final String QUEUE_NAME = "resource-uploaded";
    private static final String EXCHANGE_NAME = "resource-uploaded";
    private static final String ROUTING_KEY = "resource-uploaded";

    @Container
    public static final RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.9-management")
            .withExposedPorts(5672, 15672);

    static {
        rabbitMQContainer.start();
    }

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        // Configure RabbitMQ properties
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);

        // Add Spring Cloud Stream specific configuration
        registry.add("spring.cloud.stream.rabbit.binder.nodes", rabbitMQContainer::getHost);
        registry.add("spring.cloud.stream.default-binder", () -> "rabbit");
    }

    /**
     * Create a primary RabbitMQ connection factory that connects directly to the test container
     */
    @Bean
    @Primary
    public ConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitMQContainer.getHost());
        connectionFactory.setPort(rabbitMQContainer.getAmqpPort());
        connectionFactory.setUsername(rabbitMQContainer.getAdminUsername());
        connectionFactory.setPassword(rabbitMQContainer.getAdminPassword());
        return connectionFactory;
    }

    @Bean
    public Queue resourceUploadedQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange resourceUploadedExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding resourceUploadedBinding(Queue resourceUploadedQueue, TopicExchange resourceUploadedExchange) {
        return BindingBuilder.bind(resourceUploadedQueue).to(resourceUploadedExchange).with(ROUTING_KEY);
    }
}
