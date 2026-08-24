package com.ecom.orderservice.service;


import com.ecom.orderservice.config.RabbitMqConfig;
import com.ecom.orderservice.dto.OrderNotificationEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderEventPublisher {


    private final RabbitTemplate rabbitTemplate;


    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrderPlaced(OrderNotificationEvent event){
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE,RabbitMqConfig.ROUTING_KEY,event
        );

        System.out.println("Order event has published the message");
    }
}
