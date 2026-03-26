package com.fregrance.app.mapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fregrance.app.model.ReservationSlot;

@Mapper
public interface ReservationSlotMapper {
    List<ReservationSlot> findAvailableByDate(@Param("slotDate") LocalDate slotDate);
    ReservationSlot findByDateAndTime(@Param("slotDate") LocalDate slotDate, @Param("slotTime") LocalTime slotTime);
    ReservationSlot findById(@Param("id") Long id);
    int updateStatusIfAvailable(@Param("id") Long id, @Param("status") String status);
}