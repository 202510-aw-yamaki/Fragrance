package com.fregrance.app.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record StaffReservationSummary(
    String reservationCode,
    LocalDate slotDate,
    LocalTime slotTime,
    String slotLabel,
    String instructorName,
    String visitTypeLabel,
    String guestCountLabel,
    String slotStatus,
    String questionnaireResultCode,
    LocalDateTime createdAt
) {
}
