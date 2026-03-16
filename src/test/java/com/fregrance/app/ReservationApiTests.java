package com.fregrance.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReservationApiTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void reservationSlotsEndpointReturnsMockData() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/reservation-slots?date=" + LocalDate.now().plusDays(1),
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("slotId");
    }

    @Test
    void createReservationReturnsCreated() {
        Map<String, String> request = Map.of(
            "slotId", "2026-03-20_1030",
            "slotLabel", "3/20 10:30",
            "visitType", "初回ワークショップ",
            "guestCount", "1名",
            "staffMemo", "テスト予約"
        );

        ResponseEntity<String> response = restTemplate.postForEntity("/api/reservations", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("reservationCode");
    }
}
