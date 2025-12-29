package com.example.eventplatform.reservation;

import com.example.eventplatform.reservation.dto.ReservationRequest;
import com.example.eventplatform.reservation.dto.ReservationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:3000")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReservation(request));
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getUserReservations() {
        return ResponseEntity.ok(reservationService.getUserReservations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }
}

