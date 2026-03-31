package com.fregrance.app.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public List<StaffReservationSummary> findReservations(String visitType, String instructorName, String sortBy, String direction) {
        Comparator<StaffReservationSummary> comparator = buildComparator(sortBy);
        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return findAllReservations().stream()
            .filter(reservation -> matchesVisitType(reservation, visitType))
            .filter(reservation -> matchesInstructor(reservation, instructorName))
            .sorted(comparator)
            .toList();
    }

    public List<String> findVisitTypeOptions() {
        return findAllReservations().stream()
            .map(StaffReservationSummary::visitTypeLabel)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
    }

    public List<String> findInstructorOptions() {
        return findAllReservations().stream()
            .map(StaffReservationSummary::instructorName)
            .filter(name -> name != null && !name.isBlank())
            .distinct()
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
            normalizeVisitTypeLabel(record.getVisitTypeLabel()),
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
            normalizeVisitTypeLabel(record.getVisitTypeLabel()),
            formatGuestCount(record.getGuestCount()),
            record.getSlotStatus(),
            blankToNull(record.getQuestionnaireResultCode()),
            record.getCreatedAt()
        );
    }

    private boolean matchesVisitType(StaffReservationSummary reservation, String visitType) {
        return visitType == null || visitType.isBlank() || visitType.equals(reservation.visitTypeLabel());
    }

    private boolean matchesInstructor(StaffReservationSummary reservation, String instructorName) {
        return instructorName == null || instructorName.isBlank() || instructorName.equals(reservation.instructorName());
    }

    private Comparator<StaffReservationSummary> buildComparator(String sortBy) {
        String normalizedSort = sortBy == null || sortBy.isBlank() ? "visitDate" : sortBy;
        return switch (normalizedSort) {
            case "reservationCode" -> Comparator.comparing(StaffReservationSummary::reservationCode, Comparator.nullsLast(String::compareToIgnoreCase));
            case "visitType" -> Comparator.comparing(StaffReservationSummary::visitTypeLabel, Comparator.nullsLast(String::compareToIgnoreCase));
            case "guestCount" -> Comparator.comparing(this::parseGuestCount);
            case "instructor" -> Comparator.comparing(StaffReservationSummary::instructorName, Comparator.nullsLast(String::compareToIgnoreCase));
            case "reservationCreated" -> Comparator.comparing(StaffReservationSummary::createdAt, Comparator.nullsLast(Comparator.naturalOrder()));
            case "slotStatus" -> Comparator.comparing(StaffReservationSummary::slotStatus, Comparator.nullsLast(String::compareToIgnoreCase));
            default -> Comparator.comparing(StaffReservationSummary::slotDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(StaffReservationSummary::slotTime, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(StaffReservationSummary::reservationCode, Comparator.nullsLast(String::compareToIgnoreCase));
        };
    }

    private int parseGuestCount(StaffReservationSummary reservation) {
        String label = reservation.guestCountLabel();
        if (label == null) {
            return Integer.MAX_VALUE;
        }
        String digits = label.replaceAll("[^0-9]", "");
        return digits.isBlank() ? Integer.MAX_VALUE : Integer.parseInt(digits);
    }

    private String normalizeVisitTypeLabel(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }
        if (value.contains("初回")) {
            return "初回";
        }
        if (value.contains("再来店")) {
            return "再来店";
        }
        if (value.contains("ギフト")) {
            return "ギフト";
        }
        return value;
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