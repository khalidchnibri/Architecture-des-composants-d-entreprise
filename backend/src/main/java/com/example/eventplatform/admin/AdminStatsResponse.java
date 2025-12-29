package com.example.eventplatform.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {

    private long totalEvents;
    private long totalReservations;
    private long totalTicketsSold;
    private BigDecimal totalRevenue;
}


