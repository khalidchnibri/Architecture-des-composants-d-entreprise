package com.example.eventplatform.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dateTime;
    private String location;
    private int totalTickets;
    private int availableTickets;
    private BigDecimal ticketPrice;
    private String organizerUsername;
    private Set<String> participantUsernames;
}

