package com.fregrance.app.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import com.fregrance.app.dto.ReservationSlotResponse;
import com.fregrance.app.mapper.ReservationSlotMapper;
import com.fregrance.app.model.ReservationSlot;

@Service
public class ReservationSlotService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ReservationSlotMapper reservationSlotMapper;

    public ReservationSlotService(ObjectProvider<ReservationSlotMapper> reservationSlotMapperProvider) {
        this.reservationSlotMapper = reservationSlotMapperProvider.getIfAvailable();
    }

    public List<ReservationSlotResponse> findAvailableSlots(LocalDate date) {
        if (reservationSlotMapper != null) {
            List<ReservationSlot> slots = reservationSlotMapper.findAvailableByDate(date);
            if (slots != null && !slots.isEmpty()) {
                return slots.stream().map(this::toResponse).toList();
            }
        }

        return List.of(
            buildMockSlot(date, "10:30", "recommended", "Haraguchi"),
            buildMockSlot(date, "13:00", "open", "Shimizu"),
            buildMockSlot(date, "15:30", "open", "Otsuka")
        );
    }

    private ReservationSlotResponse toResponse(ReservationSlot slot) {
        String time = slot.getSlotTime().format(TIME_FORMATTER);
        String dateText = slot.getSlotDate().toString();
        return new ReservationSlotResponse(
            dateText + "_" + time.replace(":", ""),
            dateText,
            time,
            slot.getSlotDate().getMonthValue() + "/" + slot.getSlotDate().getDayOfMonth() + " " + time,
            slot.getStatus(),
            slot.getInstructorName()
        );
    }

    private ReservationSlotResponse buildMockSlot(LocalDate date, String time, String status, String instructorName) {
        String slotId = date + "_" + time.replace(":", "");
        String label = date.getMonthValue() + "/" + date.getDayOfMonth() + " " + time;
        return new ReservationSlotResponse(slotId, date.toString(), time, label, status, instructorName);
    }
}
