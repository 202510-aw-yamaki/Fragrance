package com.fregrance.app.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fregrance.app.dto.ReservationResponse;
import com.fregrance.app.dto.ReservationSlotResponse;
import com.fregrance.app.form.ReservationForm;
import com.fregrance.app.service.ReservationService;
import com.fregrance.app.service.ReservationSlotService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Validated
public class ReservationApiController {

    private final ReservationSlotService reservationSlotService;
    private final ReservationService reservationService;

    public ReservationApiController(ReservationSlotService reservationSlotService, ReservationService reservationService) {
        this.reservationSlotService = reservationSlotService;
        this.reservationService = reservationService;
    }

    @GetMapping("/reservation-slots")
    public List<ReservationSlotResponse> getAvailableSlots(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return reservationSlotService.findAvailableSlots(date);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationForm form) {
        ReservationResponse response = reservationService.createReservation(form);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/reservations/{reservationCode}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable String reservationCode) {
        ReservationResponse response = reservationService.findByReservationCode(reservationCode);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}
