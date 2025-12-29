package com.example.eventplatform.event.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EventRequest {
    private String title;
    private String description;
    private LocalDateTime dateTime;
    private String location;
    private int totalTickets;
    private BigDecimal ticketPrice;
}

