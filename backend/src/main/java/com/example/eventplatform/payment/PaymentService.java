package com.example.eventplatform.payment;

import com.example.eventplatform.notification.NotificationService;
import com.example.eventplatform.payment.dto.PaymentRequest;
import com.example.eventplatform.payment.dto.PaymentResponse;
import com.example.eventplatform.reservation.Reservation;
import com.example.eventplatform.reservation.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private NotificationService notificationService;

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public PaymentResponse processPayment(PaymentRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // Vérifier que le paiement appartient à l'utilisateur connecté
        String currentUsername = getCurrentUsername();
        if (!reservation.getUser().getUsername().equals(currentUsername)) {
            throw new RuntimeException("You can only pay for your own reservations");
        }

        // Vérifier si un paiement existe déjà
        if (paymentRepository.findByReservation(reservation).isPresent()) {
            throw new RuntimeException("Payment already exists for this reservation");
        }

        // Calculer le montant total automatiquement
        BigDecimal totalAmount = reservation.getEvent().getTicketPrice()
                .multiply(BigDecimal.valueOf(reservation.getQuantity()));

        // Simulation de paiement (80% de succès, 20% d'échec)
        PaymentStatus status = simulatePayment() ? PaymentStatus.SUCCESS : PaymentStatus.FAILURE;

        Payment payment = Payment.builder()
                .reservation(reservation)
                .amount(totalAmount)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();

        payment = paymentRepository.save(payment);

        // Envoyer notification
        if (status == PaymentStatus.SUCCESS) {
            notificationService.sendPaymentSuccessNotification(reservation.getUser().getEmail(), 
                    reservation.getUser().getUsername(), totalAmount, reservation.getEvent().getTitle());
        } else {
            notificationService.sendPaymentFailureNotification(reservation.getUser().getEmail(), 
                    reservation.getUser().getUsername(), totalAmount);
        }

        return mapToResponse(payment);
    }

    public PaymentResponse getPaymentByReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        String currentUsername = getCurrentUsername();
        if (!reservation.getUser().getUsername().equals(currentUsername)) {
            throw new RuntimeException("You can only view payments for your own reservations");
        }

        Payment payment = paymentRepository.findByReservation(reservation)
                .orElseThrow(() -> new RuntimeException("Payment not found for this reservation"));

        return mapToResponse(payment);
    }

    public List<PaymentResponse> getUserPayments() {
        String currentUsername = getCurrentUsername();
        // Récupérer toutes les réservations de l'utilisateur et leurs paiements
        return reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getUser().getUsername().equals(currentUsername))
                .map(reservation -> paymentRepository.findByReservation(reservation))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private boolean simulatePayment() {
        // Simulation: 80% de succès
        Random random = new Random();
        return random.nextDouble() < 0.8;
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .reservationId(payment.getReservation().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}

