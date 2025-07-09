package com.auction.user.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class RabbitMQConfig {
    @Value("${rabbitmq.queue.user}")
    private String userQueue;

    @Bean
    public Queue userQueue() {
        return new Queue(userQueue, true);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}

// Producer para enviar mensajes a la cola de usuario
@Component
class UserQueueProducer {
    private final RabbitTemplate rabbitTemplate;
    private final String userQueue;

    public UserQueueProducer(RabbitTemplate rabbitTemplate, @Value("${rabbitmq.queue.user}") String userQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.userQueue = userQueue;
    }

    public void sendUserMessage(String message) {
        rabbitTemplate.convertAndSend(userQueue, message);
    }
}

// Listener para recibir mensajes de la cola de usuario
@Component
class UserQueueListener {
    @RabbitListener(queues = "#{'${rabbitmq.queue.user}'}")
    public void receiveUserMessage(String message) {
        System.out.println("[UserQueueListener] Mensaje recibido: " + message);
    }
}
