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
    @Value("${rabbitmq.queue.auction}")
    private String auctionQueue;

    @Bean
    public Queue auctionQueue() {
        return new Queue(auctionQueue, true);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}

@Component
class AuctionQueueProducer {
    private final RabbitTemplate rabbitTemplate;
    private final String auctionQueue;

    public AuctionQueueProducer(RabbitTemplate rabbitTemplate, @Value("${rabbitmq.queue.auction}") String auctionQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.auctionQueue = auctionQueue;
    }

    public void sendAuctionMessage(String message) {
        rabbitTemplate.convertAndSend(auctionQueue, message);
    }
}

@Component
class AuctionQueueListener {
    @RabbitListener(queues = "#{'${rabbitmq.queue.auction}'}")
    public void receiveAuctionMessage(String message) {
        System.out.println("[AuctionQueueListener] Mensaje recibido: " + message);
    }
}
