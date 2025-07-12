package iuh.fit.se.shortenlink.integration.rabbitmq;

import iuh.fit.se.shortenlink.config.RabbitMqConfig;
import iuh.fit.se.shortenlink.dto.notification.ShareLinkMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public NotificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishShareLink(ShareLinkMessage message) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, RabbitMqConfig.ROUTING_KEY, message);
    }
}

