package com.auction.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class RabbitMQConfig {
    @Value("${rabbitmq.queue.bid}")
    private String bidQueue;

    @Bean
    public Queue bidQueue() {
        return new Queue(bidQueue, true);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}

@Component
class BidQueueProducer {
    private final RabbitTemplate rabbitTemplate;
    private final String bidQueue;

    public BidQueueProducer(RabbitTemplate rabbitTemplate, @Value("${rabbitmq.queue.bid}") String bidQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.bidQueue = bidQueue;
    }

    public void sendBidMessage(String message) {
        rabbitTemplate.convertAndSend(bidQueue, message);
    }
}

@Component
class BidQueueListener {
    @RabbitListener(queues = "#{'${rabbitmq.queue.bid}'}")
    public void receiveBidMessage(String message) {
        System.out.println("[BidQueueListener] Mensaje recibido: " + message);
    }
}
