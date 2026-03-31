package com.fregrance.app.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fregrance.app.dto.StaffReservationDetail;
import com.fregrance.app.dto.StaffReservationSummary;
import com.fregrance.app.mapper.ReservationMapper;
import com.fregrance.app.model.StaffReservationRecord;

@Service
public class StaffReservationService {

    private final ReservationMapper reservationMapper;
    private final ObjectMapper objectMapper;

    public StaffReservationService(ReservationMapper reservationMapper, ObjectMapper objectMapper) {
        this.reservationMapper = reservationMapper;
        this.objectMapper = objectMapper;
    }

    public List<StaffReservationSummary> findAllReservations() {
        return reservationMapper.findAllForStaff().stream()
            .map(this::toSummary)
            .toList();
    }

    public int countTodayReservations() {
        LocalDate today = LocalDate.now();
        return (int) findAllReservations().stream()
            .filter(reservation -> today.equals(reservation.slotDate()))
            .count();
    }

    public int countAllReservations() {
        return findAllReservations().size();
    }

    public StaffReservationDetail findReservationDetail(String reservationCode) {
        StaffReservationRecord record = reservationMapper.findDetailForStaff(reservationCode);
        if (record == null) {
            return null;
        }

        return new StaffReservationDetail(
            record.getReservationCode(),
            record.getSlotDate(),
            record.getSlotTime(),
            record.getSlotLabel(),
            record.getInstructorName(),
            record.getVisitTypeLabel(),
            formatGuestCount(record.getGuestCount()),
            record.getSlotStatus(),
            blankToNull(record.getStaffMemo()),
            blankToNull(record.getSummaryHeadline()),
            blankToNull(record.getQuestionnaireResultCode()),
            blankToNull(record.getRouteCode()),
            readStringMap(record.getStep1AnswersJson()),
            readStringMap(record.getStep2AnswersJson()),
            readIntegerMap(record.getGraphAxesJson()),
            record.getCreatedAt(),
            record.getUpdatedAt()
        );
    }

    private StaffReservationSummary toSummary(StaffReservationRecord record) {
        return new StaffReservationSummary(
            record.getReservationCode(),
            record.getSlotDate(),
            record.getSlotTime(),
            record.getSlotLabel(),
            record.getInstructorName(),
            record.getVisitTypeLabel(),
            formatGuestCount(record.getGuestCount()),
            record.getSlotStatus(),
            blankToNull(record.getQuestionnaireResultCode()),
            record.getCreatedAt()
        );
    }

    private String formatGuestCount(Integer guestCount) {
        return guestCount == null ? "-" : guestCount + "名";
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private Map<String, String> readStringMap(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<LinkedHashMap<String, String>>() {
            });
        } catch (IOException exception) {
            return Map.of();
        }
    }

    private Map<String, Integer> readIntegerMap(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<LinkedHashMap<String, Integer>>() {
            });
        } catch (IOException exception) {
            return Map.of();
        }
    }
}
