package com.ecom.notificationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfigMQ {

    public static final String EXCHANGE = "order.exchange";
    public static final String QUEUE = "order.notification.queue";
    public static final String ROUTING_KEY ="order.notification";

    @Bean
    public DirectExchange exchange(){
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue queue(){
        return new Queue(QUEUE);
    }

    @Bean
    public Binding binding(){
        return BindingBuilder
                .bind(queue())
                .to(exchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public org.springframework.amqp.rabbit.core.RabbitAdmin rabbitAdmin(
            org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory) {

        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }
}
