package com.example.eventplatform.payment;

import com.example.eventplatform.payment.dto.PaymentRequest;
import com.example.eventplatform.payment.dto.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.processPayment(request));
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<PaymentResponse> getPaymentByReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(paymentService.getPaymentByReservation(reservationId));
    }

    @GetMapping("/my-payments")
    public ResponseEntity<List<PaymentResponse>> getUserPayments() {
        return ResponseEntity.ok(paymentService.getUserPayments());
    }
}

