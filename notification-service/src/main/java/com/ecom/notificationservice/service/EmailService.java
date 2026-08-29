package com.ecom.notificationservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendNotificationToUser(
            String to,
            Long orderId,
            double amount,
            String status) {

        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException(
                    "Email address is null or empty for order: " + orderId
            );
        }

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);

        if ("PAID".equals(status)) {

            message.setSubject(
                    "Order Confirmation - Order #" + orderId
            );

            message.setText(
                    "Hello,\n\n" +
                            "Your order has been placed successfully.\n\n" +
                            "Order ID: " + orderId + "\n" +
                            "Amount: ₹" + amount + "\n" +
                            "Status: " + status + "\n\n" +
                            "Thank you for shopping with us."
            );

        } else if ("CANCELLED".equals(status)) {

            message.setSubject(
                    "Order Cancelled - Order #" + orderId
            );

            message.setText(
                    "Hello,\n\n" +
                            "Your order has been cancelled successfully.\n\n" +
                            "Order ID: " + orderId + "\n" +
                            "Amount: ₹" + amount + "\n" +
                            "Status: " + status + "\n\n" +
                            "Your payment has been refunded.\n\n" +
                            "Thank you."
            );
        }

        mailSender.send(message);
    }
}