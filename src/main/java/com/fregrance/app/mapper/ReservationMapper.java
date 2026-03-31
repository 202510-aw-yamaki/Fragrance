package com.fregrance.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fregrance.app.model.Reservation;
import com.fregrance.app.model.StaffReservationRecord;

@Mapper
public interface ReservationMapper {
    int insert(Reservation reservation);
    Reservation findByReservationCode(@Param("reservationCode") String reservationCode);
    List<StaffReservationRecord> findAllForStaff();
    StaffReservationRecord findDetailForStaff(@Param("reservationCode") String reservationCode);
}
