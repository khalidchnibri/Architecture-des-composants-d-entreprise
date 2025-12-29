package com.example.eventplatform.admin;

import com.example.eventplatform.event.EventRepository;
import com.example.eventplatform.payment.Payment;
import com.example.eventplatform.payment.PaymentRepository;
import com.example.eventplatform.payment.PaymentStatus;
import com.example.eventplatform.reservation.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AdminService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public AdminStatsResponse getStats() {
        long totalEvents = eventRepository.count();
        long totalReservations = reservationRepository.count();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        long totalTicketsSold = 0L;

        for (Payment payment : paymentRepository.findAll()) {
            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                totalRevenue = totalRevenue.add(payment.getAmount());
                if (payment.getReservation() != null) {
                    totalTicketsSold += payment.getReservation().getQuantity();
                }
            }
        }

        return AdminStatsResponse.builder()
                .totalEvents(totalEvents)
                .totalReservations(totalReservations)
                .totalTicketsSold(totalTicketsSold)
                .totalRevenue(totalRevenue)
                .build();
    }
}


