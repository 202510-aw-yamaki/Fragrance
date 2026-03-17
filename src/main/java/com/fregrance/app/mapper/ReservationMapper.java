package com.fregrance.app.mapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.fregrance.app.model.Reservation;
@Mapper
public interface ReservationMapper {
    int insert(Reservation reservation);
    Reservation findByReservationCode(@Param("reservationCode") String reservationCode);
}
