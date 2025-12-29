package com.example.eventplatform.payment;

import com.example.eventplatform.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReservation(Reservation reservation);
}


