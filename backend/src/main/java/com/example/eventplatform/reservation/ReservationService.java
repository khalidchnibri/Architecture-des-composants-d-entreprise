package com.example.eventplatform.reservation;

import com.example.eventplatform.event.Event;
import com.example.eventplatform.event.EventRepository;
import com.example.eventplatform.notification.NotificationService;
import com.example.eventplatform.payment.PaymentRepository;
import com.example.eventplatform.payment.PaymentStatus;
import com.example.eventplatform.reservation.dto.ReservationRequest;
import com.example.eventplatform.reservation.dto.ReservationResponse;
import com.example.eventplatform.user.User;
import com.example.eventplatform.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private NotificationService notificationService;

    private static final int MAX_TICKETS_PER_USER = 4;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public ReservationResponse createReservation(ReservationRequest request) {
        User user = getCurrentUser();
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Vérifier disponibilité
        int reservedTickets = reservationRepository.findByEventId(event.getId())
                .stream()
                .mapToInt(Reservation::getQuantity)
                .sum();
        int availableTickets = event.getTotalTickets() - reservedTickets;

        if (request.getQuantity() > availableTickets) {
            throw new RuntimeException("Not enough tickets available. Available: " + availableTickets);
        }

        // Vérifier limite de 4 tickets par utilisateur
        if (request.getQuantity() > MAX_TICKETS_PER_USER) {
            throw new RuntimeException("Maximum " + MAX_TICKETS_PER_USER + " tickets per reservation");
        }

        // Vérifier total de tickets déjà réservés par cet utilisateur pour cet événement
        List<Reservation> existingReservations = reservationRepository.findByUserAndEvent(user, event);
        int totalUserTickets = existingReservations.stream()
                .mapToInt(Reservation::getQuantity)
                .sum();

        if (totalUserTickets + request.getQuantity() > MAX_TICKETS_PER_USER) {
            throw new RuntimeException("You cannot reserve more than " + MAX_TICKETS_PER_USER + " tickets total for this event");
        }

        Reservation reservation = Reservation.builder()
                .event(event)
                .user(user)
                .quantity(request.getQuantity())
                .build();

        reservation = reservationRepository.save(reservation);
        
        // Envoyer notification de réservation
        notificationService.sendReservationNotification(
                user.getEmail(),
                user.getUsername(),
                event.getTitle(),
                reservation.getQuantity()
        );
        
        return mapToResponse(reservation);
    }

    public List<ReservationResponse> getUserReservations() {
        User user = getCurrentUser();
        return reservationRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        User currentUser = getCurrentUser();
        if (!reservation.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only view your own reservations");
        }
        
        return mapToResponse(reservation);
    }

    private ReservationResponse mapToResponse(Reservation reservation) {
        boolean paid = paymentRepository.findByReservation(reservation)
                .map(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .orElse(false);

        return ReservationResponse.builder()
                .id(reservation.getId())
                .eventId(reservation.getEvent().getId())
                .eventTitle(reservation.getEvent().getTitle())
                .username(reservation.getUser().getUsername())
                .quantity(reservation.getQuantity())
                .totalAmount(reservation.getEvent().getTicketPrice()
                        .multiply(java.math.BigDecimal.valueOf(reservation.getQuantity())))
                .paid(paid)
                .build();
    }
}

