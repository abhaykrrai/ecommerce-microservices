package com.ecom.notificationservice.config;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitDebug {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @PostConstruct
    public void init() {
        System.out.println("Rabbit Host = " + host);
        System.out.println("Rabbit Port = " + port);
    }
}
