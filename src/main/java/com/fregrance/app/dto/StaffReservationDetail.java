package com.fregrance.app.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

public record StaffReservationDetail(
    String reservationCode,
    LocalDate slotDate,
    LocalTime slotTime,
    String slotLabel,
    String instructorName,
    String visitTypeLabel,
    String guestCountLabel,
    String slotStatus,
    String staffMemo,
    String summaryHeadline,
    String questionnaireResultCode,
    String routeCode,
    Map<String, String> step1Answers,
    Map<String, String> step2Answers,
    Map<String, Integer> graphAxes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean vipCustomerFlag
) {
    public boolean hasQuestionnaire() {
        return questionnaireResultCode != null && !questionnaireResultCode.isBlank();
    }
}