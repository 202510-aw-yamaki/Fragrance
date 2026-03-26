package com.fregrance.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReservationApiTests {

    private static final LocalDate SLOT_API_TEST_DATE = LocalDate.of(2099, 12, 31);
    private static final LocalDate RESERVATION_TEST_DATE = LocalDate.of(2099, 12, 30);
    private static final Pattern RESERVATION_CODE_PATTERN = Pattern.compile("\"reservationCode\"\s*:\s*\"([^\"]+)\"");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM reservations WHERE reservation_slot_id IN (SELECT id FROM reservation_slots WHERE slot_date IN (?, ?))",
            RESERVATION_TEST_DATE, SLOT_API_TEST_DATE);
        jdbcTemplate.update("DELETE FROM reservation_slots WHERE slot_date IN (?, ?)", RESERVATION_TEST_DATE, SLOT_API_TEST_DATE);
    }

    @Test
    void reservationSlotsEndpointReturnsDatabaseDataWhenSlotsExist() {
        jdbcTemplate.update("DELETE FROM reservation_slots WHERE slot_date = ?", SLOT_API_TEST_DATE);
        jdbcTemplate.update(
            "INSERT INTO reservation_slots (slot_date, slot_time, status, instructor_name, created_at, updated_at) VALUES (?, '18:00:00', 'open', 'DbVerifier', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
            SLOT_API_TEST_DATE
        );

        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/reservation-slots?date=" + SLOT_API_TEST_DATE,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("2099-12-31_1800");
        assertThat(response.getBody()).contains("DbVerifier");
        assertThat(response.getBody()).doesNotContain("2099-12-31_1030");
    }

    @Test
    void createReservationPersistsToDatabaseWhenSlotExists() {
        jdbcTemplate.update("DELETE FROM reservation_slots WHERE slot_date = ?", RESERVATION_TEST_DATE);
        jdbcTemplate.update(
            "INSERT INTO reservation_slots (slot_date, slot_time, status, instructor_name, created_at, updated_at) VALUES (?, '10:30:00', 'open', 'DbPersist', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
            RESERVATION_TEST_DATE
        );

        Map<String, String> request = Map.of(
            "slotId", "2099-12-30_1030",
            "slotLabel", "12/30 10:30",
            "visitType", "初回ワークショップ",
            "guestCount", "1名",
            "staffMemo", "テスト予約"
        );

        ResponseEntity<String> response = restTemplate.postForEntity("/api/reservations", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String reservationCode = extractReservationCode(response.getBody());
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM reservations WHERE reservation_code = ?",
            Integer.class,
            reservationCode
        );
        assertThat(count).isEqualTo(1);
    }

    private String extractReservationCode(String responseBody) {
        Matcher matcher = RESERVATION_CODE_PATTERN.matcher(responseBody);
        assertThat(matcher.find()).isTrue();
        return matcher.group(1);
    }
}