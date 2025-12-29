package com.example.eventplatform.reservation;

import com.example.eventplatform.event.Event;
import com.example.eventplatform.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    int countByEvent(Event event);

    List<Reservation> findByUserAndEvent(User user, Event event);
    
    List<Reservation> findByEventId(Long eventId);
    
    List<Reservation> findByUserId(Long userId);
}


