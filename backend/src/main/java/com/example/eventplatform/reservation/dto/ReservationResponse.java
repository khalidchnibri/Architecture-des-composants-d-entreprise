package com.example.eventplatform.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private String username;
    private int quantity;
    private BigDecimal totalAmount;
    private boolean paid;
}

