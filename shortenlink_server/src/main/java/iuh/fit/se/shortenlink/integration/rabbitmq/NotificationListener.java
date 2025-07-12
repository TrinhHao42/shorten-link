package iuh.fit.se.shortenlink.integration.rabbitmq;

import iuh.fit.se.shortenlink.config.RabbitMqConfig;
import iuh.fit.se.shortenlink.dto.notification.ShareLinkMessage;
import iuh.fit.se.shortenlink.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private final NotificationService notificationService;

    public NotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE)
    public void consume(ShareLinkMessage message) {
        notificationService.consumeShareLink(message);
    }
}

