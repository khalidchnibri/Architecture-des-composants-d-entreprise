package com.example.eventplatform.event;

import com.example.eventplatform.event.dto.EventRequest;
import com.example.eventplatform.event.dto.EventResponse;
import com.example.eventplatform.payment.PaymentRepository;
import com.example.eventplatform.reservation.Reservation;
import com.example.eventplatform.reservation.ReservationRepository;
import com.example.eventplatform.user.Role;
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
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public EventResponse createEvent(EventRequest request) {
        User organizer = getCurrentUser();
        
        // Vérifier que l'utilisateur a le rôle ORGANIZER ou ADMIN
        boolean isOrganizer = organizer.getRoles().contains(Role.ROLE_ORGANIZER);
        boolean isAdmin = organizer.getRoles().contains(Role.ROLE_ADMIN);
        
        if (!isOrganizer && !isAdmin) {
            throw new RuntimeException("Seuls les organisateurs peuvent créer des événements");
        }
        
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .dateTime(request.getDateTime())
                .location(request.getLocation())
                .totalTickets(request.getTotalTickets())
                .ticketPrice(request.getTicketPrice())
                .organizer(organizer)
                .build();

        event = eventRepository.save(event);
        return mapToResponse(event);
    }

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return mapToResponse(event);
    }

    public EventResponse updateEvent(Long id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        User currentUser = getCurrentUser();
        if (!event.getOrganizer().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only the organizer can update this event");
        }

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setDateTime(request.getDateTime());
        event.setLocation(request.getLocation());
        event.setTotalTickets(request.getTotalTickets());
        event.setTicketPrice(request.getTicketPrice());

        event = eventRepository.save(event);
        return mapToResponse(event);
    }

    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        User currentUser = getCurrentUser();
        if (!event.getOrganizer().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only the organizer can delete this event");
        }

        // Supprimer les paiements liés aux réservations de cet événement
        List<Reservation> reservations = reservationRepository.findByEventId(id);
        for (Reservation reservation : reservations) {
            paymentRepository.findByReservation(reservation).ifPresent(paymentRepository::delete);
        }

        // Supprimer les réservations de cet événement
        reservationRepository.deleteAll(reservations);

        // Supprimer les participants (table de jointure)
        event.getParticipants().clear();

        // Supprimer l'événement
        eventRepository.delete(event);
    }

    public EventResponse addParticipant(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        User participant = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        event.getParticipants().add(participant);
        event = eventRepository.save(event);
        return mapToResponse(event);
    }

    private EventResponse mapToResponse(Event event) {
        int reservedTickets = reservationRepository.findByEventId(event.getId())
                .stream()
                .mapToInt(r -> r.getQuantity())
                .sum();
        int availableTickets = event.getTotalTickets() - reservedTickets;

        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .dateTime(event.getDateTime())
                .location(event.getLocation())
                .totalTickets(event.getTotalTickets())
                .availableTickets(availableTickets)
                .ticketPrice(event.getTicketPrice())
                .organizerUsername(event.getOrganizer() != null ? event.getOrganizer().getUsername() : null)
                .participantUsernames(event.getParticipants().stream()
                        .map(User::getUsername)
                        .collect(Collectors.toSet()))
                .build();
    }
}

