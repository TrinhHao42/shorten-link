package iuh.fit.se.shortenlink.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "notification.exchange";
    public static final String ROUTING_KEY = "notification.share-link";
    public static final String RETRY_ROUTING_KEY = "notification.share-link.retry";
    public static final String QUEUE = "notification.queue";
    public static final String RETRY_QUEUE = "notification.retry.queue";
    public static final String DLQ = "notification.dlq";

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(QUEUE, true, false, false, Map.of(
                "x-dead-letter-exchange", EXCHANGE,
                "x-dead-letter-routing-key", RETRY_ROUTING_KEY
        ));
    }

    @Bean
    public Queue notificationRetryQueue() {
        return new Queue(RETRY_QUEUE, true, false, false, Map.of(
                "x-message-ttl", 10000,
                "x-dead-letter-exchange", EXCHANGE,
                "x-dead-letter-routing-key", ROUTING_KEY
        ));
    }

    @Bean
    public Queue notificationDlq() {
        return new Queue(DLQ, true, false, false);
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue()).to(notificationExchange()).with(ROUTING_KEY);
    }

    @Bean
    public Binding notificationRetryBinding() {
        return BindingBuilder.bind(notificationRetryQueue()).to(notificationExchange()).with(RETRY_ROUTING_KEY);
    }

    @Bean
    public Binding notificationDlqBinding() {
        return BindingBuilder.bind(notificationDlq()).to(notificationExchange()).with("notification.share-link.dlq");
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter);
        return rabbitTemplate;
    }
}
