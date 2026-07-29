package com.ecom.notificationservice.config;



import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitTest {

    private final RabbitTemplate rabbitTemplate;

    public RabbitTest(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostConstruct
    public void test() {
        System.out.println("RabbitTemplate = " + rabbitTemplate);

        rabbitTemplate.convertAndSend(
                "order.exchange",
                "order.notification",
                "Hello RabbitMQ"
        );
    }
}
