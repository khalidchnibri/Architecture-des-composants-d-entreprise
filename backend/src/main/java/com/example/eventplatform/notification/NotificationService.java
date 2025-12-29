package com.example.eventplatform.notification;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendReservationNotification(String email, String username, String eventTitle, int quantity) {
        // Simulation d'envoi d'email
        System.out.println("=== EMAIL NOTIFICATION ===");
        System.out.println("To: " + email);
        System.out.println("Subject: Reservation Confirmed");
        System.out.println("Body: Hello " + username + ", your reservation for " + quantity + 
                " ticket(s) for event '" + eventTitle + "' has been confirmed.");
        System.out.println("=========================");

        // Simulation d'envoi de SMS
        System.out.println("=== SMS NOTIFICATION ===");
        System.out.println("To: " + email);
        System.out.println("Message: Reservation confirmed for " + eventTitle + " (" + quantity + " tickets)");
        System.out.println("========================");
    }

    public void sendPaymentSuccessNotification(String email, String username, java.math.BigDecimal amount, String eventTitle) {
        System.out.println("=== EMAIL NOTIFICATION ===");
        System.out.println("To: " + email);
        System.out.println("Subject: Payment Successful");
        System.out.println("Body: Hello " + username + ", your payment of " + amount + 
                " for event '" + eventTitle + "' has been processed successfully.");
        System.out.println("=========================");

        System.out.println("=== SMS NOTIFICATION ===");
        System.out.println("To: " + email);
        System.out.println("Message: Payment of " + amount + " successful for " + eventTitle);
        System.out.println("========================");
    }

    public void sendPaymentFailureNotification(String email, String username, java.math.BigDecimal amount) {
        System.out.println("=== EMAIL NOTIFICATION ===");
        System.out.println("To: " + email);
        System.out.println("Subject: Payment Failed");
        System.out.println("Body: Hello " + username + ", your payment of " + amount + 
                " has failed. Please try again.");
        System.out.println("=========================");

        System.out.println("=== SMS NOTIFICATION ===");
        System.out.println("To: " + email);
        System.out.println("Message: Payment of " + amount + " failed. Please retry.");
        System.out.println("========================");
    }
}

