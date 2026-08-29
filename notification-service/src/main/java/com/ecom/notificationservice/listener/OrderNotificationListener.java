package com.ecom.notificationservice.listener;

import com.ecom.notificationservice.config.RabbitConfigMQ;
import com.ecom.notificationservice.dto.OrderNotificationEvent;
import com.ecom.notificationservice.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderNotificationListener {

    @Autowired
    EmailService emailService;

    @RabbitListener(queues = RabbitConfigMQ.QUEUE)
    public void consumeOrderNotification(OrderNotificationEvent event){
        System.out.println("======================================");
        System.out.println("      ORDER NOTIFICATION RECEIVED");
        System.out.println("======================================");

        System.out.println("Order ID : " + event.getOrderId());
        System.out.println("User ID  : " + event.getUserId());
        System.out.println("Amount   : " + event.getAmount());
        System.out.println("Status   : " + event.getStatus());


        emailService.sendNotificationToUser(event.getEmail(), event.getOrderId(), event.getAmount(), event.getStatus());

        System.out.println("======================================");
    }
}
