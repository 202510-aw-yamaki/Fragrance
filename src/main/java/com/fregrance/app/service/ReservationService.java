package com.fregrance.app.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.fregrance.app.dto.ReservationResponse;
import com.fregrance.app.form.ReservationForm;

@Service
public class ReservationService {

    private final Map<String, ReservationResponse> reservations = new ConcurrentHashMap<>();

    public ReservationResponse createReservation(ReservationForm form) {
        String reservationCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        ReservationResponse response = new ReservationResponse(
            reservationCode,
            form.getSlotId(),
            form.getSlotLabel(),
            form.getVisitType(),
            form.getGuestCount(),
            form.getStaffMemo(),
            "CONFIRMED"
        );
        reservations.put(reservationCode, response);
        return response;
    }

    public ReservationResponse findByReservationCode(String reservationCode) {
        return reservations.get(reservationCode);
    }
}
