package resourceservice.messaging;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ResourceUploadedProducer {

    private final RabbitTemplate rabbitTemplate;

    private final String queueName;

    public ResourceUploadedProducer(RabbitTemplate rabbitTemplate,
                                    @Value("${spring.rabbitmq.queue}") String queueName) {
        this.rabbitTemplate = rabbitTemplate;
        this.queueName = queueName;
    }

    public void sendResourceId(String resourceId) {
        rabbitTemplate.convertAndSend(queueName, resourceId);
    }
}
