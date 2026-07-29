package com.ecom.orderservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "order.exchange";
    public static final String QUEUE = "order.notification.queue";
    public static final String ROUTING_KEY ="order.notification";


    @Bean
    public DirectExchange exchange(){
        return new DirectExchange(EXCHANGE);
    }

    public Queue queue(){
        return new Queue(QUEUE);
    }

    public Binding binding(){
        return BindingBuilder
                .bind(queue())
                .to(exchange())
                .with(ROUTING_KEY);
    }
}
