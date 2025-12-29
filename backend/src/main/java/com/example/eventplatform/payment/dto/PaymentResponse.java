package com.example.eventplatform.payment.dto;

import com.example.eventplatform.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long reservationId;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime createdAt;
}

