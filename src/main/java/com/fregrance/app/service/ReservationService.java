package com.fregrance.app.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fregrance.app.dto.ReservationResponse;
import com.fregrance.app.exception.InvalidRequestException;
import com.fregrance.app.exception.ReservationConflictException;
import com.fregrance.app.exception.ResourceNotFoundException;
import com.fregrance.app.form.ReservationForm;
import com.fregrance.app.mapper.ReservationMapper;
import com.fregrance.app.mapper.ReservationSlotMapper;
import com.fregrance.app.mapper.VisitTypeMapper;
import com.fregrance.app.model.Reservation;
import com.fregrance.app.model.ReservationSlot;
import com.fregrance.app.model.VisitType;

@Service
public class ReservationService {

    private final Map<String, ReservationResponse> reservations = new ConcurrentHashMap<>();
    private final ReservationMapper reservationMapper;
    private final ReservationSlotMapper reservationSlotMapper;
    private final VisitTypeMapper visitTypeMapper;

    public ReservationService(
        ObjectProvider<ReservationMapper> reservationMapperProvider,
        ObjectProvider<ReservationSlotMapper> reservationSlotMapperProvider,
        ObjectProvider<VisitTypeMapper> visitTypeMapperProvider
    ) {
        this.reservationMapper = reservationMapperProvider.getIfAvailable();
        this.reservationSlotMapper = reservationSlotMapperProvider.getIfAvailable();
        this.visitTypeMapper = visitTypeMapperProvider.getIfAvailable();
    }

    public ReservationResponse createReservation(ReservationForm form) {
        if (reservationMapper != null && reservationSlotMapper != null && visitTypeMapper != null) {
            ReservationResponse response = createReservationWithDatabase(form);
            reservations.put(response.reservationCode(), response);
            return response;
        }

        ReservationResponse response = buildResponse(generateReservationCode(), form);
        reservations.put(response.reservationCode(), response);
        return response;
    }

    public ReservationResponse findByReservationCode(String reservationCode) {
        ReservationResponse cached = reservations.get(reservationCode);
        if (cached != null) {
            return cached;
        }
        if (reservationMapper == null) {
            return null;
        }

        Reservation reservation = reservationMapper.findByReservationCode(reservationCode);
        if (reservation == null) {
            return null;
        }

        ReservationResponse response = new ReservationResponse(
            reservation.getReservationCode(),
            null,
            reservation.getSlotLabel(),
            reservation.getVisitTypeLabel(),
            reservation.getGuestCount() + "名",
            reservation.getStaffMemo(),
            "CONFIRMED"
        );
        reservations.put(response.reservationCode(), response);
        return response;
    }

    @Transactional
    private ReservationResponse createReservationWithDatabase(ReservationForm form) {
        ParsedSlot parsedSlot = parseSlotId(form.getSlotId());
        ReservationSlot slot = reservationSlotMapper.findByDateAndTime(parsedSlot.date(), parsedSlot.time());
        VisitType visitType = visitTypeMapper.findByCode(toVisitTypeCode(form.getVisitType()));

        if (slot == null) {
            throw new ResourceNotFoundException("Selected reservation slot was not found");
        }
        if (visitType == null) {
            throw new InvalidRequestException("Selected visit type is not supported");
        }
        if (!isReservableStatus(slot.getStatus())) {
            throw new ReservationConflictException("Selected reservation slot is no longer available");
        }
        if (reservationSlotMapper.updateStatusIfAvailable(slot.getId(), "reserved") == 0) {
            throw new ReservationConflictException("Selected reservation slot is no longer available");
        }

        Reservation reservation = new Reservation();
        reservation.setReservationCode(generateReservationCode());
        reservation.setReservationSlotId(slot.getId());
        reservation.setVisitTypeId(visitType.getId());
        reservation.setVisitTypeLabel(form.getVisitType());
        reservation.setGuestCount(parseGuestCount(form.getGuestCount()));
        reservation.setStaffMemo(form.getStaffMemo());
        reservation.setSummaryHeadline(form.getSummaryHeadline());
        reservation.setQuestionnaireResultCode(form.getQuestionnaireResultCode());
        reservation.setSlotLabel(form.getSlotLabel());
        reservationMapper.insert(reservation);

        return buildResponse(reservation.getReservationCode(), form);
    }

    private ReservationResponse buildResponse(String reservationCode, ReservationForm form) {
        return new ReservationResponse(
            reservationCode,
            form.getSlotId(),
            form.getSlotLabel(),
            form.getVisitType(),
            form.getGuestCount(),
            form.getStaffMemo(),
            "CONFIRMED"
        );
    }

    private String generateReservationCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ParsedSlot parseSlotId(String slotId) {
        String[] parts = slotId.split("_");
        LocalDate date = LocalDate.parse(parts[0]);
        LocalTime time = LocalTime.parse(parts[1].substring(0, 2) + ":" + parts[1].substring(2, 4));
        return new ParsedSlot(date, time);
    }

    private int parseGuestCount(String guestCount) {
        if (guestCount == null || guestCount.isBlank()) {
            return 1;
        }
        if (guestCount.startsWith("4")) {
            return 4;
        }
        return Integer.parseInt(guestCount.replace("名", ""));
    }

    private String toVisitTypeCode(String visitType) {
        return switch (visitType) {
            case "再来店の調整相談" -> "followup";
            case "ギフト相談あり" -> "gift";
            default -> "workshop";
        };
    }

    private boolean isReservableStatus(String status) {
        return "open".equals(status) || "recommended".equals(status);
    }

    private record ParsedSlot(LocalDate date, LocalTime time) {
    }
}